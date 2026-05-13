package com.paymate.paymate.module.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

/**
 * Request DTO for user registration.
 *
 * <p>Contains validation annotations that Spring checks automatically
 * when {@code @Valid} is used on the controller method parameter.
 * If any constraint fails, Spring returns a 400 Bad Request with
 * details — handled by our {@code GlobalExceptionHandler}.</p>
 *
 * @author Shubham Kumbhar
 */
@Getter
@Builder
public class RegisterRequest {

    /**
     * User's email address — used as login identifier.
     *
     * <p>{@code @NotBlank} rejects null, empty, and whitespace-only strings.
     * {@code @Email} validates the format matches an email pattern.</p>
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    private String email;

    /**
     * User's chosen password. Never stored — hashed with BCrypt.
     *
     * <p>{@code @Size(min=8)} ensures passwords are at least 8 chars.
     * The message appears in our validation error response.</p>
     */
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    /**
     * User's full name for display purposes.
     */
    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    /**
     * Optional phone number.
     * Must match international format if provided: +353871234567
     */
    // @Pattern validates against a regex
    // regexp = "^\\+[1-9]\\d{7,14}$" means:
    // ^ = start, \\+ = literal +, [1-9] = first digit not 0,
    // \\d{7,14} = 7 to 14 more digits, $ = end
    @Pattern(
            regexp = "^\\+[1-9]\\d{7,14}$",
            message = "Phone must be in international format e.g. +353871234567"
    )
    private String phone;
}