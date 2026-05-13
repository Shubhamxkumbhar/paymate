package com.paymate.paymate.module.auth.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA entity representing a refresh token issued to a user.
 *
 * <p>Refresh tokens allow users to obtain new access tokens without
 * re-entering their password. They are long-lived (30 days) but
 * stored in the database so they can be revoked instantly on logout.</p>
 *
 * <p>The actual token string is NEVER stored — only its BCrypt hash.
 * If this table is compromised, the hashes cannot be used directly.</p>
 *
 * @author Shubham Kumbhar
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    /** Unique identifier for this token record. */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * The user who owns this refresh token.
     *
     * <p>{@code @ManyToOne} means: many refresh tokens can belong to
     * one user (one user can be logged in on multiple devices).
     * {@code FetchType.LAZY} means: don't load the User object from
     * the DB until it's actually accessed — avoids unnecessary queries.</p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn specifies the foreign key column name in this table
    // This creates: refresh_tokens.user_id → users.id
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * BCrypt hash of the actual refresh token string.
     * The raw token is sent to the client but never stored here.
     */
    @Column(name = "token_hash", nullable = false, unique = true, length = 255)
    private String tokenHash;

    /**
     * When this token expires.
     * After this time the token is invalid even if not revoked.
     */
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    /**
     * Whether this token has been explicitly revoked.
     * Set to true on logout. Expired tokens are also effectively revoked.
     */
    @Column(name = "revoked", nullable = false)
    @Builder.Default
    private boolean revoked = false;

    /** When this token was created. */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Sets createdAt timestamp before first insert.
     *
     * @see User#onCreate()
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    /**
     * Returns whether this token is still valid.
     *
     * <p>A token is valid only if it has not been revoked
     * AND it has not passed its expiry time.</p>
     *
     * @return true if the token can be used, false otherwise
     */
    public boolean isValid() {
        // LocalDateTime.now().isBefore(expiresAt) checks if current
        // time is before the expiry — if yes, token hasn't expired yet
        return !revoked && LocalDateTime.now().isBefore(expiresAt);
    }
}