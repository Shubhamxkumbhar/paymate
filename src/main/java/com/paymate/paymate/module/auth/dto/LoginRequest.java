package com.paymate.paymate.module.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

/**
 * Request DTO for user login.
 *
 * @author Shubham Kumbhar
 */
@Getter
public class LoginRequest {

    /** Email address used as the login identifier. */
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    /** Plain text password — compared against stored BCrypt hash. */
    @NotBlank(message = "Password is required")
    private String password;
}