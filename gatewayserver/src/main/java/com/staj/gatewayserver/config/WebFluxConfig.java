package com.staj.gatewayserver.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * Raises Spring WebFlux's in-memory codec buffer from the default 256 KB to 20 MB.
 */
@Configuration
public class WebFluxConfig implements WebFluxConfigurer {

    // In-memory codec buffer: How many bytes an HTTP codec can load and store in RAM at one time.
    private static final int MAX_IN_MEMORY_SIZE_BYTES = 20 * 1024 * 1024; // 20 MB

    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        configurer.defaultCodecs().maxInMemorySize(MAX_IN_MEMORY_SIZE_BYTES);
    }
}
