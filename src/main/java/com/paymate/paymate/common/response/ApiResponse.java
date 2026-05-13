package com.paymate.paymate.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

/**
 * Standard API response envelope used across all PayMate endpoints.
 *
 * <p>Every endpoint returns this wrapper so the frontend always knows
 * exactly where to find data, errors, and metadata. Null fields are
 * excluded from the JSON output via {@link JsonInclude}.</p>
 *
 * <p>Usage:</p>
 * <pre>
 *   return ResponseEntity.ok(ApiResponse.success("Transfer complete", transferDto));
 *   return ResponseEntity.badRequest().body(ApiResponse.error("Invalid amount", error));
 * </pre>
 *
 * @param <T> the type of the data payload
 * @author Shubham Kumbhar
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    /** Whether the request completed successfully. */
    private final boolean success;

    /** Human-readable message describing the outcome. */
    private final String message;

    /** The response payload. Null on error responses. */
    private final T data;

    /** Error detail block. Null on success responses. */
    private final ApiError error;

    /** ISO-8601 timestamp of when this response was generated. */
    @Builder.Default
    private final String timestamp = Instant.now().toString();

    /**
     * Creates a success response with data payload.
     *
     * @param <T>     the type of the data payload
     * @param message human-readable success message
     * @param data    the response payload
     * @return a successful {@link ApiResponse} wrapping the given data
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    /**
     * Creates an error response with error detail.
     *
     * @param <T>     the type parameter (will be null for error responses)
     * @param message human-readable error message
     * @param error   structured error detail containing code and description
     * @return a failed {@link ApiResponse} with no data payload
     */
    public static <T> ApiResponse<T> error(String message, ApiError error) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .error(error)
                .build();
    }
}