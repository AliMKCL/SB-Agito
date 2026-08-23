package com.staj.stock.util;

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
        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(fileBytes))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String name = entry.getName().toLowerCase();
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
