package com.paymate.paymate.common.exception;

import com.paymate.paymate.common.response.ApiError;
import com.paymate.paymate.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * Central exception handler for all PayMate REST controllers.
 *
 * <p>Catches exceptions thrown anywhere in the application and converts
 * them into consistent {@link ApiResponse} error payloads. This means
 * controllers never need try-catch blocks for standard error cases —
 * they just throw and this handler takes care of the response.</p>
 *
 * <p>Handler priority (first match wins):</p>
 * <ol>
 *   <li>{@link PayMateException} subclasses — domain errors with known HTTP status</li>
 *   <li>{@link MethodArgumentNotValidException} — Bean Validation failures</li>
 *   <li>{@link Exception} — unexpected errors, always returns 500</li>
 * </ol>
 *
 * @author PayMate Engineering
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles all PayMate domain exceptions.
     *
     * <p>Uses the status code and error code embedded in the exception
     * so we never need to add new handler methods for new exception types —
     * just create a new subclass of {@link PayMateException}.</p>
     *
     * @param ex the domain exception thrown by a service or controller
     * @return a structured error response with the appropriate HTTP status
     */
    @ExceptionHandler(PayMateException.class)
    public ResponseEntity<ApiResponse<Void>> handlePayMateException(PayMateException ex) {
        log.warn("Domain exception: [{}] {}", ex.getErrorCode(), ex.getMessage());

        ApiError error = ApiError.builder()
                .code(ex.getErrorCode())
                .details(ex.getMessage())
                .build();

        return ResponseEntity
                .status(ex.getStatus())
                .body(ApiResponse.error(ex.getMessage(), error));
    }

    /**
     * Handles Bean Validation failures from {@code @Valid} annotated request bodies.
     *
     * <p>Collects all field-level validation errors into a single readable
     * message rather than returning just the first failure.</p>
     *
     * @param ex the validation exception containing all field errors
     * @return a 400 Bad Request response listing all validation failures
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(
            MethodArgumentNotValidException ex) {

        // Collect all field errors into "fieldName: message" format
        String details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        log.warn("Validation failed: {}", details);

        ApiError error = ApiError.builder()
                .code("VALIDATION_ERROR")
                .details(details)
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Validation failed", error));
    }

    /**
     * Catch-all handler for any unexpected exception not handled above.
     *
     * <p>Logs the full stack trace for debugging but returns a safe generic
     * message to the client — never expose internal error details in production.</p>
     *
     * @param ex any unhandled exception
     * @return a 500 Internal Server Error with a safe generic message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpectedException(Exception ex) {
        log.error("Unexpected error — this should be investigated", ex);

        ApiError error = ApiError.builder()
                .code("INTERNAL_ERROR")
                .details("An unexpected error occurred. Please try again.")
                .build();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Something went wrong", error));
    }
}