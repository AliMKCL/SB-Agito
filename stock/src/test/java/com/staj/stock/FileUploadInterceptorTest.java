package com.staj.stock;

import com.staj.stock.interceptor.FileUploadInterceptor;
import com.staj.stock.service.ClamAvScannerService;
import com.staj.stock.service.FileInspectionService;
import com.staj.stock.util.ExcelSignatureValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockMultipartHttpServletRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileUploadInterceptorTest {

    @Mock
    private ClamAvScannerService clamAvScannerService;

    @Mock
    private ExcelSignatureValidator signatureValidator;

    private FileInspectionService fileInspectionService;
    private FileUploadInterceptor interceptor;

    @BeforeEach
    void setUp() {
        fileInspectionService = new FileInspectionService(signatureValidator, clamAvScannerService);
        interceptor = new FileUploadInterceptor(fileInspectionService);
    }

    @Test
    void testPreHandle_CleanExcelFile_ReturnsTrue() throws Exception {
        MockMultipartHttpServletRequest request = new MockMultipartHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        byte[] content = "dummy excel content".getBytes();
        MockMultipartFile file = new MockMultipartFile("file", "test.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", content);
        request.addFile(file);

        when(signatureValidator.isValidExcel(any(), any())).thenReturn(true);
        when(signatureValidator.containsMacros(any())).thenReturn(false);
        when(clamAvScannerService.isClean(any())).thenReturn(true);

        boolean result = interceptor.preHandle(request, response, new Object());

        assertTrue(result);
        assertEquals(200, response.getStatus());
    }

    @Test
    void testPreHandle_MissingFileParam_ReturnsFalseAnd400() throws Exception {
        MockMultipartHttpServletRequest request = new MockMultipartHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean result = interceptor.preHandle(request, response, new Object());

        assertFalse(result);
        assertEquals(400, response.getStatus());
    }

    @Test
    void testPreHandle_InvalidSignature_ReturnsFalseAnd415() throws Exception {
        MockMultipartHttpServletRequest request = new MockMultipartHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        byte[] content = "not an excel file".getBytes();
        MockMultipartFile file = new MockMultipartFile("file", "fake.xlsx", "text/plain", content);
        request.addFile(file);

        when(signatureValidator.isValidExcel(any(), any())).thenReturn(false);

        boolean result = interceptor.preHandle(request, response, new Object());

        assertFalse(result);
        assertEquals(415, response.getStatus());
    }

    @Test
    void testPreHandle_MacroDetected_ReturnsFalseAnd400() throws Exception {
        MockMultipartHttpServletRequest request = new MockMultipartHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        byte[] content = "macro excel".getBytes();
        MockMultipartFile file = new MockMultipartFile("file", "macro.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", content);
        request.addFile(file);

        when(signatureValidator.isValidExcel(any(), any())).thenReturn(true);
        when(signatureValidator.containsMacros(any())).thenReturn(true);

        boolean result = interceptor.preHandle(request, response, new Object());

        assertFalse(result);
        assertEquals(400, response.getStatus());
    }

    @Test
    void testPreHandle_VirusDetected_ReturnsFalseAnd422() throws Exception {
        MockMultipartHttpServletRequest request = new MockMultipartHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        byte[] content = "infected content".getBytes();
        MockMultipartFile file = new MockMultipartFile("file", "virus.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", content);
        request.addFile(file);

        when(signatureValidator.isValidExcel(any(), any())).thenReturn(true);
        when(signatureValidator.containsMacros(any())).thenReturn(false);
        when(clamAvScannerService.isClean(any())).thenReturn(false);

        boolean result = interceptor.preHandle(request, response, new Object());

        assertFalse(result);
        assertEquals(422, response.getStatus());
    }

    @Test
    void testPreHandle_ScannerUnavailable_ReturnsFalseAnd503() throws Exception {
        MockMultipartHttpServletRequest request = new MockMultipartHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        byte[] content = "content".getBytes();
        MockMultipartFile file = new MockMultipartFile("file", "test.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", content);
        request.addFile(file);

        when(signatureValidator.isValidExcel(any(), any())).thenReturn(true);
        when(signatureValidator.containsMacros(any())).thenReturn(false);
        when(clamAvScannerService.isClean(any())).thenThrow(new ClamAvScannerService.ClamAvUnavailableException("daemon down", new IOException()));

        boolean result = interceptor.preHandle(request, response, new Object());

        assertFalse(result);
        assertEquals(503, response.getStatus());
    }
}
