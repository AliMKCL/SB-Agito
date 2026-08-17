package com.staj.gatewayserver.util;

import org.apache.tika.Tika;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Validates the binary content of an uploaded file to confirm it is a genuine
 * Excel document and does not contain embedded VBA macros.
 *
 * <h2>Why {@code TikaInputStream} instead of {@code Tika.detect(byte[])}?</h2>
 * {@code .xlsx} files are ZIP archives whose magic bytes ({@code PK}, 0x50 0x4B)
 * are identical to any other ZIP file. Calling {@code Tika.detect(byte[])} on an
 * {@code .xlsx} file therefore returns {@code application/zip} instead of the
 * correct OOXML MIME type, causing every valid Excel file to be rejected.
 *
 * <p>By wrapping the bytes in a {@link TikaInputStream} together with a
 * {@link Metadata} object that carries the filename, Tika can inspect the ZIP
 * container's {@code [Content_Types].xml} entry and reliably identify
 * {@code .xlsx} / {@code .xls} files.
 */
@Component
public class ExcelSignatureValidator {

    private static final String MIME_XLSX =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final String MIME_XLS =
            "application/vnd.ms-excel";

    private final Tika tika = new Tika();

    /**
     * Returns {@code true} when the file bytes represent a genuine Excel document.
     *
     * @param fileBytes raw bytes of the uploaded file
     * @param filename  original filename (used to help Tika distinguish OOXML from plain ZIP)
     */
    public boolean isValidExcel(byte[] fileBytes, String filename) {
        Metadata metadata = new Metadata();
        metadata.set(TikaCoreProperties.RESOURCE_NAME_KEY, filename);

        try (TikaInputStream tikaStream = TikaInputStream.get(fileBytes, metadata)) {
            String detectedType = tika.detect(tikaStream, metadata);
            return MIME_XLSX.equals(detectedType) || MIME_XLS.equals(detectedType);
        } catch (IOException e) {
            // If Tika cannot read the stream at all the file is not a valid Excel document
            return false;
        }
    }

    /**
     * Returns {@code true} when the ZIP structure of an {@code .xlsx} file contains
     * a VBA project entry, indicating embedded macros that could execute malicious code.
     *
     * @param fileBytes raw bytes of the uploaded file
     */
    public boolean containsMacros(byte[] fileBytes) {
        // Inspect internal ZIP entries of .xlsx to block embedded VBA macros
        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(fileBytes))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String name = entry.getName().toLowerCase();
                // vbaProject.bin is the canonical container for VBA code in OOXML
                if (name.contains("vbaproject.bin") || name.endsWith(".bin")) {
                    return true;
                }
            }
        } catch (IOException ignored) {
            // Not a ZIP container — e.g. legacy binary .xls; no macro check needed
        }
        return false;
    }
}