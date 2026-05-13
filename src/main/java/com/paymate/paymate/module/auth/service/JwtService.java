package com.paymate.paymate.module.auth.service;

import com.paymate.paymate.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

/**
 * Service responsible for JWT token generation and validation.
 *
 * <p>Uses the JJWT library to create and parse JSON Web Tokens.
 * All tokens are signed with HMAC-SHA256 using the secret key
 * from application configuration.</p>
 *
 * <p>Access tokens contain the user's ID and email as claims.
 * They are short-lived (15 minutes) to limit damage if stolen.</p>
 *
 * @author PayMate Engineering
 */
@Slf4j
// @Service marks this as a Spring service component
// Spring will create one instance and inject it wherever needed
@Service
@RequiredArgsConstructor
public class JwtService {

    /** JWT configuration properties bound from application.yml */
    private final JwtProperties jwtProperties;

    /**
     * Generates a signed JWT access token for the given user.
     *
     * <p>The token payload (claims) contains:</p>
     * <ul>
     *   <li>subject: the user's UUID as a string</li>
     *   <li>email: the user's email address</li>
     *   <li>issuedAt: current timestamp</li>
     *   <li>expiration: current time + access token expiry</li>
     * </ul>
     *
     * @param userId the UUID of the user to create a token for
     * @param email  the user's email address to embed as a claim
     * @return signed JWT token string
     */
    public String generateAccessToken(UUID userId, String email) {
        // System.currentTimeMillis() returns current time in milliseconds
        // We add the expiry duration to get the expiry timestamp
        Date now = new Date(System.currentTimeMillis());
        Date expiry = new Date(System.currentTimeMillis() + jwtProperties.getAccessTokenExpiry());

        return Jwts.builder()
                // subject: who this token is about (user's ID)
                .subject(userId.toString())
                // claim: add custom data to the token payload
                .claim("email", email)
                // issuedAt: when the token was created
                .issuedAt(now)
                // expiration: when the token stops being valid
                .expiration(expiry)
                // signWith: sign the token with our secret key
                // This creates the signature part of the JWT
                .signWith(getSigningKey())
                // compact: build the token into the final string
                .compact();
    }

    /**
     * Validates a JWT token and returns its claims if valid.
     *
     * <p>Validation checks:</p>
     * <ul>
     *   <li>Signature is valid — token wasn't tampered with</li>
     *   <li>Token is not expired</li>
     *   <li>Token format is correct</li>
     * </ul>
     *
     * @param token the JWT token string to validate
     * @return the token claims if valid, null if invalid
     */
    public Claims validateToken(String token) {
        try {
            // parseSignedClaims verifies the signature AND expiry
            // If either check fails, it throws a JwtException
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException ex) {
            // Log at debug level — invalid tokens are common (expired, etc.)
            // We don't want to fill logs with warnings for every expired token
            log.debug("JWT validation failed: {}", ex.getMessage());
            return null;
        }
    }

    /**
     * Extracts the user ID from a validated JWT token.
     *
     * @param token the JWT token string
     * @return the user's UUID, or null if the token is invalid
     */
    public UUID extractUserId(String token) {
        Claims claims = validateToken(token);
        if (claims == null) {
            return null;
        }
        // getSubject() returns the "subject" claim we set in generateAccessToken
        // UUID.fromString converts the string back to a UUID object
        return UUID.fromString(claims.getSubject());
    }

    /**
     * Builds the signing key from the configured secret string.
     *
     * <p>HMAC-SHA256 requires a key of at least 256 bits (32 bytes).
     * We convert the secret string to bytes using UTF-8 encoding
     * and wrap it in a SecretKey object the JJWT library accepts.</p>
     *
     * @return the SecretKey used to sign and verify tokens
     */
    private SecretKey getSigningKey() {
        // getBytes(StandardCharsets.UTF_8) converts String to byte[]
        // Keys.hmacShaKeyFor wraps the bytes in a SecretKey
        byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}