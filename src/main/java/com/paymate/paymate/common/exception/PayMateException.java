package com.paymate.paymate.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Base exception for all PayMate domain errors.
 *
 * <p>All custom exceptions in the application extend this class.
 * It carries an {@link HttpStatus} so the {@code GlobalExceptionHandler}
 * can set the correct HTTP response code without a large if-else chain.</p>
 *
 * <p>Never throw this class directly — always use a specific subclass
 * that clearly names the error condition (e.g. {@code InsufficientFundsException}).</p>
 *
 * @author PayMate Engineering
 */
@Getter
public class PayMateException extends RuntimeException {

    /**
     * The HTTP status code this exception maps to in API responses.
     * Set by subclasses to control the response code.
     */
    private final HttpStatus status;

    /**
     * Machine-readable error code included in the {@link com.paymate.paymate.common.response.ApiError}.
     * Should be SCREAMING_SNAKE_CASE (e.g. "INSUFFICIENT_FUNDS").
     */
    private final String errorCode;

    /**
     * Constructs a new PayMate exception.
     *
     * @param message   human-readable error description
     * @param status    the HTTP status code to return to the client
     * @param errorCode machine-readable error code for the frontend
     */
    public PayMateException(String message, HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }
}