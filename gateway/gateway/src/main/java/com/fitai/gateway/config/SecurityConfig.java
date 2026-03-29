package com.fitai.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(org.springframework.security.config.Customizer.withDefaults())
                .authorizeExchange(exchanges -> exchanges
                        // Allow all CORS preflight requests
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Allow user registration without auth
                        .pathMatchers(HttpMethod.POST, "/api/users/register", "/api/users/keycloak-register").permitAll()
                        // Allow GET on user profile validation without auth (internal call)
                        .pathMatchers(HttpMethod.GET, "/api/users/*/validate").permitAll()
                        // Secure all other endpoints
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> {})
                )
                .build();
    }
}
