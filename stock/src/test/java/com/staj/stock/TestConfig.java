package com.staj.stock;

import com.google.api.services.gmail.Gmail;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class TestConfig {

    @Bean
    public Gmail gmailService() {
        return Mockito.mock(Gmail.class);
    }
}
