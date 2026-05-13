package com.paymate.paymate.module.auth.service;

import com.paymate.paymate.common.exception.DuplicateResourceException;
import com.paymate.paymate.common.exception.InvalidCredentialsException;
import com.paymate.paymate.common.exception.ResourceNotFoundException;
import com.paymate.paymate.config.JwtProperties;
import com.paymate.paymate.module.auth.domain.RefreshToken;
import com.paymate.paymate.module.auth.domain.User;
import com.paymate.paymate.module.auth.dto.*;
import com.paymate.paymate.module.auth.repository.RefreshTokenRepository;
import com.paymate.paymate.module.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Core authentication service handling registration, login,
 * token refresh, and logout operations.
 *
 * <p>This service is the single source of truth for auth logic.
 * Controllers delegate to this service — they never contain
 * business logic themselves.</p>
 *
 * <p>{@code @Transactional} on methods means: if anything fails
 * mid-method, ALL database changes in that method are rolled back.
 * For example, if we save the user but fail to save the refresh token,
 * the user record is also undone — no partial data in the DB.</p>
 *
 * @author PayMate Engineering
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    /** Talks to the users table */
    private final UserRepository userRepository;

    /** Talks to the refresh_tokens table */
    private final RefreshTokenRepository refreshTokenRepository;

    /** Generates and validates JWT tokens */
    private final JwtService jwtService;

    /**
     * BCrypt password encoder — injected from SecurityConfig.
     * Used to hash passwords on register and verify on login.
     */
    private final PasswordEncoder passwordEncoder;

    /** JWT config for token expiry values */
    private final JwtProperties jwtProperties;

    /**
     * Registers a new user account.
     *
     * <p>Steps:</p>
     * <ol>
     *   <li>Check email is not already taken</li>
     *   <li>Check phone is not already taken (if provided)</li>
     *   <li>Hash the password with BCrypt</li>
     *   <li>Save the user to the database</li>
     *   <li>Return the user profile (no password)</li>
     * </ol>
     *
     * @param request the registration details from the client
     * @return the created user's profile
     * @throws DuplicateResourceException if email or phone already exists
     */
    @Transactional
    public UserResponse register(RegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());

        // Step 1: Check email uniqueness
        // existsByEmail runs: SELECT COUNT(*) > 0 FROM users WHERE email = ?
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }

        // Step 2: Check phone uniqueness (only if phone was provided)
        // request.getPhone() != null checks if the phone field was sent
        if (request.getPhone() != null && userRepository.existsByPhone(request.getPhone())) {
            throw new DuplicateResourceException("User", "phone", request.getPhone());
        }

        // Step 3: Hash the password
        // passwordEncoder.encode() runs BCrypt on the plain text password
        // The result looks like: $2a$10$N9qo8uLOickgx2ZMRZoMye...
        // It is IMPOSSIBLE to reverse this back to "mypassword123"
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // Step 4: Build and save the User entity
        // User.builder() uses the @Builder pattern from Lombok
        User user = User.builder()
                .email(request.getEmail())
                .phone(request.getPhone())
                .fullName(request.getFullName())
                .passwordHash(hashedPassword)
                .build();

        // userRepository.save() runs: INSERT INTO users (...) VALUES (...)
        // It returns the saved user with the generated UUID id populated
        User savedUser = userRepository.save(user);
        log.info("User registered successfully with id: {}", savedUser.getId());

        // Step 5: Return DTO — never return the entity directly
        return UserResponse.from(savedUser);
    }

    /**
     * Authenticates a user and issues JWT tokens.
     *
     * <p>Steps:</p>
     * <ol>
     *   <li>Find user by email</li>
     *   <li>Verify password matches the stored hash</li>
     *   <li>Generate access token (15 min)</li>
     *   <li>Generate and store refresh token (30 days)</li>
     *   <li>Return both tokens</li>
     * </ol>
     *
     * @param request login credentials from the client
     * @return access and refresh tokens
     * @throws InvalidCredentialsException if email not found or password wrong
     */
    @Transactional
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());

        // Step 1: Find user by email
        // orElseThrow: if Optional is empty, throw the exception
        // We use InvalidCredentialsException (not "user not found")
        // to avoid revealing whether the email exists
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(InvalidCredentialsException::new);

        // Step 2: Verify password
        // passwordEncoder.matches(rawPassword, storedHash) runs BCrypt
        // comparison — it hashes the raw password and compares to stored hash
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        // Step 3: Generate JWT access token
        String accessToken = jwtService.generateAccessToken(user.getId(), user.getEmail());

        // Step 4: Generate and store refresh token
        // UUID.randomUUID() creates a cryptographically random token
        String rawRefreshToken = UUID.randomUUID().toString();

        // Store the HASH of the refresh token — never the raw value
        // If DB is compromised, attackers cannot use the hashes directly
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .tokenHash(passwordEncoder.encode(rawRefreshToken))
                .expiresAt(LocalDateTime.now()
                        .plusSeconds(jwtProperties.getRefreshTokenExpiry() / 1000))
                .build();

        refreshTokenRepository.save(refreshToken);

        log.info("Login successful for user: {}", user.getId());

        // Step 5: Return both tokens to the client
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(rawRefreshToken)
                .expiresIn(jwtProperties.getAccessTokenExpiry() / 1000)
                .build();
    }

    /**
     * Revokes all refresh tokens for a user (logout from all devices).
     *
     * <p>Access tokens cannot be invalidated (they're stateless) but
     * they expire in 15 minutes. Revoking refresh tokens means the
     * user cannot obtain new access tokens after their current one expires.</p>
     *
     * @param userId the ID of the user logging out
     */
    @Transactional
    public void logout(UUID userId) {
        log.info("Logging out user: {}", userId);
        // Revoke all refresh tokens for this user in one UPDATE query
        refreshTokenRepository.revokeAllUserTokens(userId);
    }

    /**
     * Returns the profile of a user by their ID.
     *
     * @param userId the UUID of the user to retrieve
     * @return the user's profile DTO
     * @throws ResourceNotFoundException if no user exists with this ID
     */
    @Transactional(readOnly = true)
    public UserResponse getProfile(UUID userId) {
        // readOnly = true is a performance hint — Hibernate won't track
        // changes to entities loaded in a read-only transaction
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return UserResponse.from(user);
    }
}