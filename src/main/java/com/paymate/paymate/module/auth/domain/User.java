package com.paymate.paymate.module.auth.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA entity representing a registered PayMate user.
 *
 * <p>Maps to the {@code users} table in PostgreSQL. This class is the
 * central domain object for the auth module — it represents any person
 * or business using PayMate, regardless of their role.</p>
 *
 * <p>Passwords are NEVER stored here in plain text. Only BCrypt hashes
 * are persisted. The original password is never retrievable.</p>
 *
 * @author Shubham Kumbhar
 */
@Getter
@Setter
// @Builder lets us create User objects like:
// User.builder().email("a@b.com").fullName("John").build()
// Much cleaner than calling setters one by one
@Builder
// JPA requires a no-argument constructor to create objects when
// reading from the database — Lombok generates it automatically
@NoArgsConstructor
// @AllArgsConstructor is needed because @Builder requires it
// when @NoArgsConstructor is also present
@AllArgsConstructor
// @Entity tells JPA: "This class maps to a database table"
@Entity
// @Table specifies which table — "users" in our case
// Without this, JPA would look for a table named "user" (class name)
@Table(name = "users")
public class User {

    /**
     * Unique identifier for this user.
     * UUID prevents attackers from guessing other users' IDs.
     */
    // @Id marks this field as the primary key
    @Id
    // @GeneratedValue tells JPA how to generate the ID
    // GenerationType.AUTO lets JPA/Hibernate pick the strategy
    // For PostgreSQL with uuid-ossp extension, this uses the DB function
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * User's email address. Used as the login identifier.
     * Unique across the entire system.
     */
    // @Column maps this field to a specific column
    // unique = true adds a DB-level unique constraint
    // nullable = false means this column cannot be null in the DB
    @Column(name = "email", unique = true, nullable = false, length = 255)
    private String email;

    /**
     * Optional phone number for SMS notifications and 2FA.
     * Unique when provided — two users cannot share a phone number.
     */
    @Column(name = "phone", unique = true, length = 20)
    private String phone;

    /**
     * User's display name shown in the UI and transaction history.
     */
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    /**
     * BCrypt hash of the user's password.
     * The original password is never stored or retrievable.
     */
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    /**
     * User's role controlling what they can access.
     *
     * @see Role
     */
    // @Enumerated(EnumType.STRING) stores the enum as a string in the DB
    // e.g. Role.USER is stored as "USER" not as 0
    // Always use STRING not ORDINAL — if you reorder the enum,
    // ORDINAL values change and corrupt your data
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    @Builder.Default
    private Role role = Role.USER;

    /**
     * KYC (Know Your Customer) verification status.
     * New users start as PENDING until identity is verified.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "kyc_status", nullable = false, length = 20)
    @Builder.Default
    private KycStatus kycStatus = KycStatus.PENDING;

    /**
     * Whether this account is active.
     * False means the account is soft-deleted or suspended.
     * We never hard-delete users — financial records must be preserved.
     */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;

    /**
     * Timestamp when this record was first created.
     * Set once on insert, never updated.
     */
    // updatable = false means Hibernate will never include this column
    // in UPDATE statements — created_at should never change
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when this record was last modified.
     * Updated on every save.
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Automatically sets timestamps before the entity is first saved.
     *
     * <p>{@code @PrePersist} is a JPA lifecycle callback — JPA calls
     * this method automatically just before inserting a new row.
     * This guarantees created_at and updated_at are always set,
     * even if the caller forgot to set them.</p>
     */
    @PrePersist
    protected void onCreate() {
        // LocalDateTime.now() gets the current date and time
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * Automatically updates the updated_at timestamp before every save.
     *
     * <p>{@code @PreUpdate} is called by JPA just before updating
     * an existing row. This ensures updated_at always reflects
     * the last modification time without any manual effort.</p>
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}