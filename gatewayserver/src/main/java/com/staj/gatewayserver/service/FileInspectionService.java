package com.staj.gatewayserver.service;

import com.staj.gatewayserver.util.ExcelSignatureValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

/**
 * Service responsible for inspecting an uploaded Excel file before it is forwarded downstream.
 * Orchestrates three layers of validation in sequence:
 * <ol>
 *   <li>Magic-byte / MIME-type verification (Apache Tika)</li>
 *   <li>VBA macro detection (ZIP entry inspection)</li>
 *   <li>Antivirus scanning (ClamAV INSTREAM protocol)</li>
 * </ol>
 */
@Service
public class FileInspectionService {

    private static final Logger log = LoggerFactory.getLogger(FileInspectionService.class);

    /**
     * 15 MB — enforced both as a pre-read hint and after the full body is buffered.
     */
    private static final long MAX_FILE_SIZE_BYTES = 15L * 1024 * 1024;

    private final ExcelSignatureValidator signatureValidator;
    private final ClamAvScannerService clamAvScannerService;

    public FileInspectionService(ExcelSignatureValidator signatureValidator,
                                 ClamAvScannerService clamAvScannerService) {
        this.signatureValidator = signatureValidator;
        this.clamAvScannerService = clamAvScannerService;
    }

    /**
     * Reads the bytes of a multipart file part, enforces size limits, and runs all
     * security checks.
     *
     * @param filePart          the incoming multipart file
     * @param contentLengthHint the value of the {@code Content-Length} header, or
     *                          {@code null} when the header is absent
     * @return a {@link Mono} that emits the raw file bytes when all checks pass,
     *         or terminates with a {@link ResponseStatusException} describing the
     *         specific violation
     */
    public Mono<byte[]> inspectAndRead(FilePart filePart, Long contentLengthHint) {
        String filename = filePart.filename();
        log.debug("[FILE UPLOAD] Inspection started — file='{}', Content-Length hint={}",
                filename, contentLengthHint != null ? contentLengthHint + " bytes" : "absent");

        // Fast-fail: reject by header alone before touching the body
        if (contentLengthHint != null && contentLengthHint > MAX_FILE_SIZE_BYTES) {
            log.warn("[FILE UPLOAD] Rejected before reading body — file='{}' exceeds 15 MB limit "
                    + "(Content-Length={})", filename, contentLengthHint);
            return Mono.error(new ResponseStatusException(
                    HttpStatus.PAYLOAD_TOO_LARGE, "File exceeds the 15 MB limit."));
        }

        return readBytes(filePart)
                .flatMap(bytes -> validateSize(filename, bytes))
                .flatMap(bytes -> validateSignature(filename, bytes))
                .flatMap(bytes -> validateNoMacros(filename, bytes))
                .flatMap(bytes -> scanForViruses(filename, bytes))
                .doOnSuccess(bytes ->
                        log.info("[FILE UPLOAD] All checks passed — file='{}', size={} bytes",
                                filename, bytes.length));
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Collects all {@link org.springframework.core.io.buffer.DataBuffer} chunks
     * from the multipart file into a single byte array, releasing each buffer
     * immediately after reading to avoid Netty direct-memory leaks.
     */
    private Mono<byte[]> readBytes(FilePart filePart) {
        log.debug("[FILE UPLOAD] Reading body bytes for file='{}'", filePart.filename());
        return filePart.content()
                .reduce(new byte[0], (accumulated, dataBuffer) -> {
                    try {
                        byte[] chunk = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(chunk);
                        byte[] merged = new byte[accumulated.length + chunk.length];
                        System.arraycopy(accumulated, 0, merged, 0, accumulated.length);
                        System.arraycopy(chunk, 0, merged, accumulated.length, chunk.length);
                        return merged;
                    } finally {
                        DataBufferUtils.release(dataBuffer);
                    }
                });
    }

    /** Checks the actual byte length once the body has been fully read. */
    private Mono<byte[]> validateSize(String filename, byte[] fileBytes) {
        log.debug("[FILE UPLOAD] Size check — file='{}', actual size={} bytes", filename, fileBytes.length);
        if (fileBytes.length > MAX_FILE_SIZE_BYTES) {
            log.warn("[FILE UPLOAD] REJECTED (size) — file='{}' is {} bytes, limit is {} bytes",
                    filename, fileBytes.length, MAX_FILE_SIZE_BYTES);
            return Mono.error(new ResponseStatusException(
                    HttpStatus.PAYLOAD_TOO_LARGE, "File exceeds the 15 MB limit."));
        }
        return Mono.just(fileBytes);
    }

    /**
     * Verifies the binary magic bytes / MIME type using Apache Tika.
     * Rejects files that are not genuine Excel documents regardless of their extension.
     */
    private Mono<byte[]> validateSignature(String filename, byte[] fileBytes) {
        log.debug("[FILE UPLOAD] Signature check (magic bytes / MIME) — file='{}'", filename);
        if (!signatureValidator.isValidExcel(fileBytes, filename)) {
            log.warn("[FILE UPLOAD] REJECTED (signature) — file='{}' is not a valid Excel document", filename);
            return Mono.error(new ResponseStatusException(
                    HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                    "File must be a valid Excel document (.xlsx or .xls)."));
        }
        log.debug("[FILE UPLOAD] Signature OK — file='{}'", filename);
        return Mono.just(fileBytes);
    }

    /**
     * Inspects the ZIP internal structure of .xlsx files to detect embedded VBA
     * macros, which can execute malicious code on the server.
     */
    private Mono<byte[]> validateNoMacros(String filename, byte[] fileBytes) {
        log.debug("[FILE UPLOAD] Macro detection — file='{}'", filename);
        if (signatureValidator.containsMacros(fileBytes)) {
            log.warn("[FILE UPLOAD] REJECTED (macros) — file='{}' contains VBA macros", filename);
            return Mono.error(new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Macro-enabled Excel files are not permitted."));
        }
        log.debug("[FILE UPLOAD] No macros found — file='{}'", filename);
        return Mono.just(fileBytes);
    }

    /** Streams the file bytes to ClamAV via TCP for virus signature scanning. */
    private Mono<byte[]> scanForViruses(String filename, byte[] fileBytes) {
        log.debug("[FILE UPLOAD] ClamAV scan — file='{}'", filename);
        try {
            if (!clamAvScannerService.isClean(fileBytes)) {
                // ClamAV was reached and positively flagged the file
                log.warn("[FILE UPLOAD] REJECTED (antivirus) — ClamAV flagged file='{}'", filename);
                return Mono.error(new ResponseStatusException(
                        HttpStatus.UNPROCESSABLE_ENTITY,
                        "File was rejected by the antivirus scanner."));
            }
        } catch (ClamAvScannerService.ClamAvUnavailableException e) {
            // ClamAV daemon could not be reached — distinguish from a real detection
            log.error("[FILE UPLOAD] REJECTED (scanner unavailable) — file='{}': {}", filename, e.getMessage());
            return Mono.error(new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "The antivirus scanner is currently unavailable. Please try again later."));
        }
        log.debug("[FILE UPLOAD] ClamAV clean — file='{}'", filename);
        return Mono.just(fileBytes);
    }
}
