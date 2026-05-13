package com.paymate.paymate.module.auth.repository;

import com.paymate.paymate.module.auth.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for {@link User} entity database operations.
 *
 * <p>Extends {@link JpaRepository} which automatically provides:
 * save(), findById(), findAll(), delete(), count(), and more.
 * Spring Data JPA generates the SQL implementation at startup —
 * no SQL or implementation code needed for standard operations.</p>
 *
 * <p>Custom query methods follow Spring Data naming conventions:
 * {@code findBy + FieldName} generates a SELECT WHERE query.
 * Spring reads the method name and generates SQL automatically.</p>
 *
 * @author Shubham Kumbhar
 */
// @Repository marks this as a Spring-managed data access component
// It also enables Spring to translate database exceptions into
// Spring's unified DataAccessException hierarchy
@Repository
// JpaRepository<User, UUID> means:
// - We're working with User entities
// - The primary key type is UUID
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Finds a user by their email address.
     *
     * <p>Spring Data JPA reads the method name "findByEmail" and
     * automatically generates: SELECT * FROM users WHERE email = ?</p>
     *
     * <p>Returns {@link Optional} because the user may not exist.
     * Optional forces callers to handle the "not found" case explicitly,
     * preventing NullPointerExceptions.</p>
     *
     * @param email the email address to search for
     * @return an Optional containing the user if found, empty otherwise
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a user with the given email already exists.
     *
     * <p>Generates: SELECT COUNT(*) > 0 FROM users WHERE email = ?</p>
     *
     * <p>Used during registration to prevent duplicate accounts
     * without loading the full User object unnecessarily.</p>
     *
     * @param email the email address to check
     * @return true if a user with this email exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Checks if a user with the given phone number already exists.
     *
     * <p>Generates: SELECT COUNT(*) > 0 FROM users WHERE phone = ?</p>
     *
     * @param phone the phone number to check
     * @return true if a user with this phone exists, false otherwise
     */
    boolean existsByPhone(String phone);
}