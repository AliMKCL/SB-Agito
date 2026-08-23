package com.staj.stock.service;

import com.staj.stock.util.ExcelSignatureValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

/**
 * Service responsible for inspecting an uploaded Excel file before it is processed.
 * Orchestrates four layers of validation in sequence:
 * <ol>
 *   <li>File size check (&lt;= 15 MB)</li>
 *   <li>Magic-byte / MIME-type verification (Apache Tika)</li>
 *   <li>VBA macro detection (ZIP entry inspection)</li>
 *   <li>Antivirus scanning (ClamAV INSTREAM protocol)</li>
 * </ol>
 */
@Service
public class FileInspectionService {

    private static final Logger log = LoggerFactory.getLogger(FileInspectionService.class);

    /** 15 MB maximum file size limit. */
    private static final long MAX_FILE_SIZE_BYTES = 15L * 1024 * 1024;

    private final ExcelSignatureValidator signatureValidator;
    private final ClamAvScannerService clamAvScannerService;

    public FileInspectionService(ExcelSignatureValidator signatureValidator,
                                 ClamAvScannerService clamAvScannerService) {
        this.signatureValidator = signatureValidator;
        this.clamAvScannerService = clamAvScannerService;
    }

    /**
     * Inspects a {@link MultipartFile} and throws {@link ResponseStatusException}
     * with an appropriate HTTP status code if any check fails.
     *
     * @param file the uploaded multipart file
     * @throws ResponseStatusException if any inspection check fails
     */
    public void inspect(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing or empty file.");
        }

        String filename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "unknown.xlsx";
        long size = file.getSize();

        log.debug("[FILE INSPECTION] Inspecting file='{}', size={} bytes", filename, size);

        // 1. Size check
        if (size > MAX_FILE_SIZE_BYTES) {
            log.warn("[FILE INSPECTION] REJECTED (size) — file='{}' is {} bytes, limit is {} bytes",
                    filename, size, MAX_FILE_SIZE_BYTES);
            throw new ResponseStatusException(
                    HttpStatus.PAYLOAD_TOO_LARGE, "File exceeds the 15 MB limit.");
        }

        byte[] fileBytes;
        try {
            fileBytes = file.getBytes();
        } catch (IOException e) {
            log.error("[FILE INSPECTION] Failed to read file bytes for '{}': {}", filename, e.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Could not read uploaded file content.");
        }

        // 2. Signature / MIME check via Apache Tika
        if (!signatureValidator.isValidExcel(fileBytes, filename)) {
            log.warn("[FILE INSPECTION] REJECTED (signature) — file='{}' is not a valid Excel document", filename);
            throw new ResponseStatusException(
                    HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                    "File must be a valid Excel document (.xlsx or .xls).");
        }

        // 3. Macro check
        if (signatureValidator.containsMacros(fileBytes)) {
            log.warn("[FILE INSPECTION] REJECTED (macros) — file='{}' contains VBA macros", filename);
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Macro-enabled Excel files are not permitted.");
        }

        // 4. Antivirus scan via ClamAV
        try {
            if (!clamAvScannerService.isClean(fileBytes)) {
                log.warn("[FILE INSPECTION] REJECTED (antivirus) — ClamAV flagged file='{}'", filename);
                throw new ResponseStatusException(
                        HttpStatus.UNPROCESSABLE_ENTITY,
                        "File was rejected by the antivirus scanner.");
            }
        } catch (ClamAvScannerService.ClamAvUnavailableException e) {
            log.error("[FILE INSPECTION] REJECTED (scanner unavailable) — file='{}': {}", filename, e.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "The antivirus scanner is currently unavailable. Please try again later.");
        }

        log.info("[FILE INSPECTION] All checks passed — file='{}', size={} bytes", filename, fileBytes.length);
    }
}
