package com.paymate.paymate.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security configuration for PayMate.
 *
 * <p>Configures stateless JWT-based authentication. Key decisions:</p>
 * <ul>
 *   <li>CSRF disabled — safe for stateless REST APIs that don't use cookies for auth</li>
 *   <li>Sessions disabled — we use JWT, not server-side sessions</li>
 *   <li>Public endpoints: health, register, login</li>
 *   <li>Everything else requires a valid JWT</li>
 * </ul>
 *
 * @author PayMate Engineering
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    /**
     * Defines which endpoints are public and which require authentication.
     *
     * <p>Session policy is STATELESS — Spring Security will never create
     * or use an HTTP session. Every request must include a valid JWT.</p>
     *
     * @param http the HttpSecurity builder
     * @return the configured security filter chain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF — not needed for stateless JWT APIs
                // CSRF protects against browser-based attacks using cookies
                // Since we use Authorization header (not cookies), CSRF isn't applicable
                .csrf(AbstractHttpConfigurer::disable)

                // Configure which URLs need authentication
                .authorizeHttpRequests(auth -> auth
                        // These endpoints are public — no token needed
                        .requestMatchers(
                                "/api/v1/health",
                                "/api/v1/auth/register",
                                "/api/v1/auth/login",
                                "/api/v1/auth/refresh"
                        ).permitAll()
                        // Every other endpoint requires authentication
                        .anyRequest().authenticated()
                )

                // STATELESS: never create HTTP sessions
                // Each request must carry its own JWT — server remembers nothing
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }

    /**
     * BCrypt password encoder bean.
     *
     * <p>BCrypt is the industry standard for password hashing.
     * Strength parameter 12 means 2^12 = 4096 iterations —
     * slow enough to resist brute force, fast enough for normal use.</p>
     *
     * <p>Declaring this as a {@code @Bean} lets Spring inject it
     * into AuthService automatically via constructor injection.</p>
     *
     * @return a BCryptPasswordEncoder with strength 12
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCryptPasswordEncoder(12) — 12 is the "cost factor"
        // Higher = slower to hash = harder to brute force
        // 10 is the default, 12 is more secure, 14+ is too slow for login
        return new BCryptPasswordEncoder(12);
    }
}