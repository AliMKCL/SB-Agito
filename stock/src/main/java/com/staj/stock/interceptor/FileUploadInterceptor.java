package com.staj.stock.interceptor;

import com.staj.stock.service.FileInspectionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Intercepts Excel file upload requests before they reach the controller.
 * Executes file inspection checks (size, MIME type, macros, antivirus) and
 * only allows clean requests to proceed to {@code StockExcelController}.
 */
@Component
public class FileUploadInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(FileUploadInterceptor.class);
    private static final String FILE_PARAM_NAME = "file";

    private final FileInspectionService fileInspectionService;

    public FileUploadInterceptor(FileInspectionService fileInspectionService) {
        this.fileInspectionService = fileInspectionService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("[FILE INTERCEPTOR] Intercepted {} {}", request.getMethod(), request.getRequestURI());

        if (request instanceof MultipartHttpServletRequest multipartRequest) {
            MultipartFile file = multipartRequest.getFile(FILE_PARAM_NAME);

            if (file == null || file.isEmpty()) {
                log.warn("[FILE INTERCEPTOR] Missing required multipart field '{}'", FILE_PARAM_NAME);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType(MediaType.TEXT_PLAIN_VALUE);
                response.getWriter().write("Missing required multipart field '" + FILE_PARAM_NAME + "'.");
                return false;
            }

            try {
                fileInspectionService.inspect(file);
                log.info("[FILE INTERCEPTOR] File inspection passed — allowing request to reach controller");
                return true; // true --> Allow request to pass to the controller.
            } catch (ResponseStatusException ex) {
                log.warn("[FILE INTERCEPTOR] File inspection rejected — status={}, reason='{}'",
                        ex.getStatusCode(), ex.getReason());
                response.setStatus(ex.getStatusCode().value());
                response.setContentType(MediaType.TEXT_PLAIN_VALUE);
                response.getWriter().write(ex.getReason() != null ? ex.getReason() : ex.getMessage());
                return false;
            } catch (Exception ex) {
                log.error("[FILE INTERCEPTOR] Unexpected error during file inspection: {}", ex.getMessage(), ex);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setContentType(MediaType.TEXT_PLAIN_VALUE);
                response.getWriter().write("Internal server error during file inspection.");
                return false;
            }
        }

        return true;
    }
}
