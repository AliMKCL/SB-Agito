package com.staj.gatewayserver.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * Scans uploaded file bytes for malware by streaming them to a ClamAV daemon
 * using the INSTREAM TCP protocol.
 *
 * <h2>ClamAV INSTREAM protocol</h2>
 * <ol>
 *   <li>Send the null-terminated command {@code zINSTREAM\0}</li>
 *   <li>Send the file in chunks, each prefixed with a 4-byte big-endian length header</li>
 *   <li>Send a zero-length chunk (4 zero bytes) to signal end-of-stream</li>
 *   <li>Read a single response line: {@code "stream: OK"} = clean, anything else = flagged</li>
 * </ol>
 *
 * <h2>Error handling policy</h2>
 * A <strong>connectivity failure</strong> (ClamAV unreachable, socket timeout, DNS error)
 * is fundamentally different from a positive virus detection. When the scanner cannot
 * be reached, a {@link ClamAvUnavailableException} is thrown so the caller can return
 * an appropriate HTTP 503 rather than silently treating the file as infected.
 */
@Service
public class ClamAvScannerService {

    private static final Logger log = LoggerFactory.getLogger(ClamAvScannerService.class);

    private static final int CHUNK_SIZE = 2048;
    private static final int SOCKET_TIMEOUT_MS = 5000;

    @Value("${clamav.host:localhost}")
    private String clamAvHost;

    @Value("${clamav.port:3310}")
    private int clamAvPort;

    /**
     * Streams {@code data} to ClamAV and returns {@code true} when the file is clean.
     *
     * @param data raw file bytes to scan
     * @return {@code true} if ClamAV reports {@code "stream: OK"}
     * @throws ClamAvUnavailableException if the ClamAV daemon cannot be reached or
     *                                    the connection fails mid-transfer
     */
    public boolean isClean(byte[] data) {
        log.debug("[CLAMAV] Connecting to {}:{} to scan {} bytes", clamAvHost, clamAvPort, data.length);

        try (Socket socket = new Socket(clamAvHost, clamAvPort);
             OutputStream out = new BufferedOutputStream(socket.getOutputStream());
             InputStream in = new BufferedInputStream(socket.getInputStream())) {

            socket.setSoTimeout(SOCKET_TIMEOUT_MS);

            // Send the INSTREAM command
            out.write("zINSTREAM\0".getBytes(StandardCharsets.US_ASCII));
            out.flush();

            // Stream the file in fixed-size chunks, each preceded by its 4-byte length
            for (int i = 0; i < data.length; i += CHUNK_SIZE) {
                int length = Math.min(CHUNK_SIZE, data.length - i);
                out.write(ByteBuffer.allocate(4).putInt(length).array());
                out.write(data, i, length);
            }

            // Zero-length chunk signals end-of-stream to ClamAV
            out.write(new byte[]{0, 0, 0, 0});
            out.flush();

            // Read ClamAV's verdict
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.US_ASCII));
            String response = reader.readLine();
            log.debug("[CLAMAV] Response: '{}'", response);

            boolean clean = response != null && response.trim().equals("stream: OK");
            if (!clean) {
                log.warn("[CLAMAV] File flagged — ClamAV response: '{}'", response);
            }
            return clean;

        } catch (IOException e) {
            // Connectivity failure — ClamAV is unreachable or the connection was dropped.
            // This is NOT a virus detection; throw a distinct exception so the caller can
            // return HTTP 503 (scanner unavailable) rather than 422 (infected file).
            log.error("[CLAMAV] Could not reach ClamAV at {}:{} — {}", clamAvHost, clamAvPort, e.getMessage());
            throw new ClamAvUnavailableException(
                    "ClamAV scanner is unreachable at " + clamAvHost + ":" + clamAvPort, e);
        }
    }

    /**
     * Thrown when the ClamAV daemon cannot be contacted due to a network or
     * configuration error (as opposed to a positive virus detection).
     */
    public static class ClamAvUnavailableException extends RuntimeException {
        public ClamAvUnavailableException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}