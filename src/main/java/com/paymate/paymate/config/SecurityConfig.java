package com.paymate.paymate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security configuration for PayMate.
 *
 * <p><strong>Sprint 1 version:</strong> All endpoints are intentionally
 * open to allow development and testing without authentication.
 * This entire class will be replaced in Sprint 2 with JWT-based
 * authentication and role-based access control.</p>
 *
 * <p><strong>Never use this configuration in production.</strong></p>
 *
 * @author PayMate Engineering
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configures an open security filter chain for Sprint 1 development.
     *
     * <p>Disables CSRF (safe for stateless REST APIs using JWT) and
     * permits all requests without authentication. Sprint 2 replaces
     * this with JWT validation and endpoint-level access rules.</p>
     *
     * @param http the {@link HttpSecurity} builder provided by Spring
     * @return the configured {@link SecurityFilterChain} bean
     * @throws Exception if the security configuration cannot be built
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                );
        return http.build();
    }
}