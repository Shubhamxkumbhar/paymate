package com.paymate.paymate.module.auth.controller;

import com.paymate.paymate.common.response.ApiResponse;
import com.paymate.paymate.module.auth.dto.*;
import com.paymate.paymate.module.auth.service.AuthService;
import com.paymate.paymate.module.auth.service.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller exposing authentication endpoints.
 *
 * <p>This controller is intentionally thin — it only handles
 * HTTP concerns (parsing requests, building responses, HTTP status codes).
 * All business logic lives in {@link AuthService}.</p>
 *
 * <p>Rule: if a controller method has more than 5 lines of logic,
 * that logic belongs in the service layer, not here.</p>
 *
 * @author PayMate Engineering
 */
@Slf4j
// @RestController = @Controller + @ResponseBody
// @Controller: this class handles HTTP requests
// @ResponseBody: return values are written directly to the HTTP response as JSON
@RestController
// @RequestMapping sets the base URL path for all methods in this class
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    /**
     * Registers a new user account.
     *
     * <p>{@code @Valid} triggers Bean Validation on the request body.
     * If any @NotBlank, @Email etc. constraints fail, Spring automatically
     * returns a 400 error via our GlobalExceptionHandler — this method
     * never even executes.</p>
     *
     * @param request the registration details, validated automatically
     * @return 201 Created with the new user's profile
     */
    @PostMapping("/register")
    // ResponseEntity lets us control the HTTP status code
    // ResponseEntity<ApiResponse<UserResponse>> means:
    // HTTP response containing JSON of ApiResponse containing UserResponse
    public ResponseEntity<ApiResponse<UserResponse>> register(
            // @RequestBody: parse the JSON request body into RegisterRequest
            // @Valid: run all validation annotations on RegisterRequest
            @Valid @RequestBody RegisterRequest request) {

        UserResponse user = authService.register(request);

        // HttpStatus.CREATED = 201 — correct status for resource creation
        // 200 OK is for reads, 201 Created is for POST that creates something
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Registration successful", user));
    }

    /**
     * Authenticates a user and returns JWT tokens.
     *
     * @param request login credentials
     * @return 200 OK with access token and refresh token
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        AuthResponse tokens = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", tokens));
    }

    /**
     * Returns the authenticated user's profile.
     *
     * <p>Extracts the user ID from the JWT token in the
     * Authorization header. The token was already validated
     * by the security filter chain before reaching this method.</p>
     *
     * @param authHeader the Authorization header value (Bearer token)
     * @return 200 OK with the user's profile
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getProfile(
            // @RequestHeader extracts a specific HTTP header
            // "Authorization" header contains: "Bearer eyJhbGciOiJIUzI1NiJ9..."
            @RequestHeader("Authorization") String authHeader) {

        // Extract the token — remove "Bearer " prefix (7 characters)
        String token = authHeader.substring(7);
        UUID userId = jwtService.extractUserId(token);

        UserResponse user = authService.getProfile(userId);
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved", user));
    }

    /**
     * Logs out the authenticated user by revoking all refresh tokens.
     *
     * @param authHeader the Authorization header containing the JWT
     * @return 200 OK confirming logout
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        UUID userId = jwtService.extractUserId(token);

        authService.logout(userId);
        return ResponseEntity.ok(ApiResponse.success("Logout successful", null));
    }
}