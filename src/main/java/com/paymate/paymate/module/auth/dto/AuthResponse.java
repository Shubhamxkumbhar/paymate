package com.paymate.paymate.module.auth.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * Response DTO returned after successful login or token refresh.
 *
 * <p>Contains both token types:</p>
 * <ul>
 *   <li>Access token: short-lived (15 min), sent with every API request</li>
 *   <li>Refresh token: long-lived (30 days), used only to get new access tokens</li>
 * </ul>
 *
 * @author Shubham Kumbhar
 */
@Getter
@Builder
public class AuthResponse {

    /**
     * JWT access token to include in Authorization header.
     * Format: "Bearer eyJhbGciOiJIUzI1NiJ9..."
     * Expires in 15 minutes.
     */
    private String accessToken;

    /**
     * Refresh token to obtain new access tokens.
     * Store securely — in httpOnly cookie ideally, not localStorage.
     * Expires in 30 days.
     */
    private String refreshToken;

    /**
     * Access token validity in seconds.
     * Lets the client know when to refresh.
     */
    private long expiresIn;

    /** Token type — always "Bearer" for JWT. */
    @Builder.Default
    private String tokenType = "Bearer";
}