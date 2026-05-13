package com.paymate.paymate.module.auth.repository;

import com.paymate.paymate.module.auth.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for {@link RefreshToken} entity database operations.
 *
 * @author Shubham Kumbhar
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    /**
     * Finds a refresh token record by its hash value.
     *
     * <p>During token refresh, the client sends the raw token.
     * We hash it and look it up here to find the stored record.</p>
     *
     * @param tokenHash the BCrypt hash of the token to find
     * @return an Optional containing the token record if found
     */
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    /**
     * Revokes all refresh tokens belonging to a specific user.
     *
     * <p>Used during logout to invalidate all sessions across
     * all devices — not just the current one.</p>
     *
     * <p>{@code @Modifying} is required for UPDATE/DELETE queries —
     * it tells Spring Data this query modifies data, not just reads it.</p>
     *
     * <p>{@code @Query} lets us write custom JPQL (Java Persistence
     * Query Language) when the method name convention isn't enough.
     * JPQL uses class names and field names, not table/column names.</p>
     *
     * @param userId the UUID of the user whose tokens to revoke
     */
    @Modifying
    // JPQL query — uses Java class name (RefreshToken) and field name (user.id)
    // not table name (refresh_tokens) and column name (user_id)
    @Query("UPDATE RefreshToken rt SET rt.revoked = true WHERE rt.user.id = :userId")
    void revokeAllUserTokens(UUID userId);
}