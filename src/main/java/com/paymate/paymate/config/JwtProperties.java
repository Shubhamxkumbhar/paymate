package com.paymate.paymate.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Strongly-typed configuration properties for JWT token settings.
 *
 * <p>Binds to the {@code jwt.*} namespace in {@code application.yml}.
 * Using @ConfigurationProperties instead of @Value gives us:</p>
 * <ul>
 *   <li>Type safety — expiry values are Longs not Strings</li>
 *   <li>Testability — inject a test instance with different values</li>
 *   <li>Validation — can add @NotNull, @Min constraints</li>
 * </ul>
 *
 * @author Shubham Kumbhar
 */
@Getter
@Setter
@Component
// @ConfigurationProperties tells Spring: "bind the 'jwt' block from
// application.yml to the fields of this class automatically"
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * Secret key used to sign JWT tokens.
     * Must be at least 32 characters long for HMAC-SHA256.
     * In production this comes from an environment variable.
     */
    private String secret;

    /**
     * Access token validity duration in milliseconds.
     * Default: 900000ms = 15 minutes.
     * Short expiry limits damage if a token is stolen.
     */
    private long accessTokenExpiry;

    /**
     * Refresh token validity duration in milliseconds.
     * Default: 2592000000ms = 30 days.
     * Longer lived but stored in DB and revocable on logout.
     */
    private long refreshTokenExpiry;

}
