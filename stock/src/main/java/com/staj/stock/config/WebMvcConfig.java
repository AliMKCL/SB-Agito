package com.staj.stock.config;

import com.staj.stock.interceptor.FileUploadInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC configuration to register interceptors for the stock service.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final FileUploadInterceptor fileUploadInterceptor;

    public WebMvcConfig(FileUploadInterceptor fileUploadInterceptor) {
        this.fileUploadInterceptor = fileUploadInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(fileUploadInterceptor)
                .addPathPatterns("/apiAdmin/uploadExcel");
    }
}
