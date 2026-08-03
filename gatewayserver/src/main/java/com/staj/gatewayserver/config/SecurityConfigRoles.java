package com.staj.gatewayserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

// The old version of SecurityConfig with Client Credentials Grant Type Flow + RBAC.

/*
@EnableWebFluxSecurity
@Configuration
*/
public class SecurityConfigRoles {
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity serverHttpSecurity){
        serverHttpSecurity.authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/stock/apiAdmin/**").hasRole("ADMIN")
                        .pathMatchers("/stock/apiConsumer/**").hasRole("CONSUMER")
                        .pathMatchers("/stock/apiAnalyst/**").hasRole("ANALYST")
                        .pathMatchers("/product/apiAdmin/**").hasRole("ADMIN")
                        .pathMatchers("/product/apiConsumer/**").hasRole("CONSUMER")
                        .anyExchange().permitAll()) // Allow all else without authentication (actuator endpoints)


                // Configure the resource server:
                .oauth2ResourceServer(oAuth2ResourceServerSpec -> oAuth2ResourceServerSpec
                        // .jwt(Customizer.withDefaults())) // Use default jwt token configurations. NO LONGER DEFAULT due to roles.
                        .jwt(jwtSpec -> jwtSpec.jwtAuthenticationConverter(grantedAuthoritiesExtractor() )));
        ;

        serverHttpSecurity.csrf(csrfSpec -> csrfSpec.disable()); // Disable csrf protection.
        return serverHttpSecurity.build();
    }

    private Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor() {
        JwtAuthenticationConverter jwtAuthenticationConverter =
                new JwtAuthenticationConverter();
        // Give JwtAuthenticationConverter the logic to convert jwt token into GrantedAuthorities format (get roles).
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter
                (new KeycloakRoleConverter());
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }
}
