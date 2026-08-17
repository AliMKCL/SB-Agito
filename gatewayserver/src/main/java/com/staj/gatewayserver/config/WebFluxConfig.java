package com.staj.gatewayserver.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * Raises Spring WebFlux's in-memory codec buffer from the default 256 KB to 20 MB.
 *
 * <p>Without this, multipart file uploads are rejected with HTTP 415 at the codec
 * level — before the controller runs and with no application log output.
 *
 * <p>This limit must stay in sync with {@code spring.codec.max-in-memory-size} in
 * {@code application.yaml} and {@code MAX_FILE_SIZE_BYTES} in {@link com.staj.gatewayserver.service.FileInspectionService}.
 */
@Configuration
public class WebFluxConfig implements WebFluxConfigurer {

    private static final int MAX_IN_MEMORY_SIZE_BYTES = 20 * 1024 * 1024; // 20 MB

    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        configurer.defaultCodecs().maxInMemorySize(MAX_IN_MEMORY_SIZE_BYTES);
    }
}
