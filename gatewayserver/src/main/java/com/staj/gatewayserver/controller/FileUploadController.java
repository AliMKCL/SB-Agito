package com.staj.gatewayserver.controller;

import com.staj.gatewayserver.service.FileInspectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Exposes the file-upload endpoint at {@code POST /files/upload}.
 *
 * <p>Validation (size, signature, macros, antivirus) is delegated to
 * {@link FileInspectionService}. On success the file is forwarded to the
 * Stock Service at {@code /apiAdmin/uploadExcel}.
 */
@RestController
@RequestMapping("/files")
public class FileUploadController {

    private static final Logger log = LoggerFactory.getLogger(FileUploadController.class);

    private final FileInspectionService fileInspectionService;
    private final RestTemplate restTemplate;

    public FileUploadController(FileInspectionService fileInspectionService,
                                RestTemplate restTemplate) {
        this.fileInspectionService = fileInspectionService;
        this.restTemplate = restTemplate;
    }

    /**
     * Receives an Excel file, runs it through the inspection pipeline,
     * then forwards it to the Stock Service.
     *
     * @param filePart      multipart field named {@code "file"}
     * @param contentLength {@code Content-Length} header for an early size check
     * @param authHeader    {@code Authorization} header forwarded to the Stock Service
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<Void>> uploadFile(
            @RequestPart("file") FilePart filePart,
            @RequestHeader(value = "Content-Length", required = false) Long contentLength,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        log.info("[FILE UPLOAD] Request received — file='{}', Content-Length={}, auth={}",
                filePart.filename(), contentLength, authHeader != null ? "present" : "absent");

        return fileInspectionService.inspectAndRead(filePart, contentLength)
                .flatMap(fileBytes -> forwardToStockService(filePart.filename(), fileBytes, authHeader))
                .doOnSuccess(response ->
                        log.info("[FILE UPLOAD] Completed — file='{}', status={}",
                                filePart.filename(), response.getStatusCode()))
                .onErrorResume(ResponseStatusException.class, ex -> {
                    log.warn("[FILE UPLOAD] Validation failed — file='{}', status={}, reason='{}'",
                            filePart.filename(), ex.getStatusCode(), ex.getReason());
                    return Mono.just(ResponseEntity.status(ex.getStatusCode()).build());
                })
                .onErrorResume(throwable -> {
                    log.error("[FILE UPLOAD] Unexpected error — file='{}': {}",
                            filePart.filename(), throwable.getMessage(), throwable);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    /**
     * Writes the validated bytes to a named temp file, then POSTs it to the Stock Service
     * using a load-balanced {@link RestTemplate} and {@link FileSystemResource}.
     *
     * <p>The blocking {@code RestTemplate} call is offloaded to
     * {@code Schedulers.boundedElastic()} to avoid blocking the WebFlux event-loop thread.
     * The temp file is always deleted in the {@code finally} block.
     */
    private Mono<ResponseEntity<Void>> forwardToStockService(String filename,
                                                              byte[] fileBytes,
                                                              String authHeader) {
        return Mono.fromCallable(() -> {
            String extension = filename.contains(".")
                    ? filename.substring(filename.lastIndexOf('.'))
                    : ".xlsx";
            Path tempFile = Files.createTempFile("gateway-upload-", extension);

            try {
                Files.write(tempFile, fileBytes);

                // Preserve the original filename in the Content-Disposition part header
                FileSystemResource fileResource = new FileSystemResource(tempFile.toFile()) {
                    @Override public String getFilename() { return filename; }
                };

                MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
                body.add("file", fileResource);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.MULTIPART_FORM_DATA);
                if (authHeader != null) {
                    headers.set(HttpHeaders.AUTHORIZATION, authHeader);
                }

                log.debug("[FILE UPLOAD] Forwarding to Stock Service — file='{}', size={} bytes",
                        filename, fileBytes.length);

                ResponseEntity<Void> response = restTemplate.exchange(
                        "http://stock/apiAdmin/uploadExcel",
                        HttpMethod.POST,
                        new HttpEntity<>(body, headers),
                        Void.class);

                log.debug("[FILE UPLOAD] Stock Service responded — file='{}', status={}",
                        filename, response.getStatusCode());
                return response;

            } catch (HttpStatusCodeException ex) {
                log.warn("[FILE UPLOAD] Stock Service returned error — file='{}', status={}",
                        filename, ex.getStatusCode());
                return ResponseEntity.status(ex.getStatusCode()).<Void>build();
            } finally {
                try {
                    Files.deleteIfExists(tempFile);
                } catch (IOException e) {
                    log.warn("[FILE UPLOAD] Failed to delete temp file '{}': {}", tempFile, e.getMessage());
                }
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }
}
