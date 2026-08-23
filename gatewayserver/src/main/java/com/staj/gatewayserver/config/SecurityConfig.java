package com.staj.gatewayserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationEntryPoint;
import org.springframework.security.web.server.util.matcher.MediaTypeServerWebExchangeMatcher;

import java.util.Set;

/**
 * Security configuration for the gateway server.
 *
 * <p>Supports two authentication modes simultaneously:
 * <ul>
 *   <li><strong>Browser</strong> — unauthenticated requests that accept {@code text/html}
 *       are redirected to the Keycloak login page via OAuth2 authorization code flow.</li>
 *   <li><strong>API clients (Postman, services)</strong> — requests with an
 *       {@code Authorization: Bearer <token>} header are validated as JWTs against the
 *       Keycloak JWKS endpoint. Unauthenticated API requests receive {@code 401}.</li>
 * </ul>
 */
@EnableWebFluxSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/stock/**").authenticated()
                        .pathMatchers("/product/**").authenticated()
                        .anyExchange().permitAll())

                // Browser flow: redirects unauthenticated browser requests to Keycloak
                .oauth2Login(Customizer.withDefaults())

                // API flow: validates Bearer JWT tokens from REST clients
                .oauth2ResourceServer(spec -> spec.jwt(Customizer.withDefaults()))

                // Dispatch unauthenticated requests: browser → Keycloak redirect, API → 401
                .exceptionHandling(spec -> spec.authenticationEntryPoint(delegatingEntryPoint()))

                .csrf(csrf -> csrf.disable());

        return http.build();
    }

    /**
     * Routes unauthenticated requests to the correct entry point based on {@code Accept}:
     * <ul>
     *   <li>Browsers ({@code Accept: text/html}) → redirect to {@code /oauth2/authorization/keycloak}</li>
     *   <li>API clients ({@code Accept: application/json} or absent) → {@code 401 Unauthorized}</li>
     * </ul>
     */
    private ServerAuthenticationEntryPoint delegatingEntryPoint() {
        RedirectServerAuthenticationEntryPoint browserEntryPoint =
                new RedirectServerAuthenticationEntryPoint("/oauth2/authorization/keycloak");
        HttpStatusServerEntryPoint apiEntryPoint =
                new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED);

        MediaTypeServerWebExchangeMatcher htmlMatcher =
                new MediaTypeServerWebExchangeMatcher(MediaType.TEXT_HTML);
        htmlMatcher.setIgnoredMediaTypes(Set.of(MediaType.ALL));

        return (exchange, ex) -> htmlMatcher.matches(exchange)
                .flatMap(match -> match.isMatch()
                        ? browserEntryPoint.commence(exchange, ex)
                        : apiEntryPoint.commence(exchange, ex));
    }
}
