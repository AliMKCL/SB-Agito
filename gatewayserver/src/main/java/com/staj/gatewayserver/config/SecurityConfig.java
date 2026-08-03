package com.staj.gatewayserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity serverHttpSecurity) {
        serverHttpSecurity.authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/stock/**").authenticated()
                        .pathMatchers("/product/**").authenticated()
                        .anyExchange().permitAll())

                // Enable OAuth2 Login for browser redirection
                .oauth2Login(Customizer.withDefaults())

                // Enable OAuth2 Resource Server for direct bearer tokens (REST clients / Postman)
                .oauth2ResourceServer(oAuth2ResourceServerSpec -> oAuth2ResourceServerSpec
                        .jwt(Customizer.withDefaults())); // Default jwt token configurations

        serverHttpSecurity.csrf(csrfSpec -> csrfSpec.disable()); // Disable csrf protection (for dev)
        return serverHttpSecurity.build();
    }
}

