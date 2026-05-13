package com.paymate.paymate.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when login credentials are incorrect.
 *
 * <p>Intentionally vague message — never tell the client whether
 * it was the email or password that was wrong. That information
 * helps attackers enumerate valid accounts.</p>
 *
 * <p>Maps to HTTP 401 Unauthorized.</p>
 *
 * @author Shubham Kumbhar
 */
public class InvalidCredentialsException extends PayMateException {

    /**
     * Creates an invalid credentials exception with a deliberately
     * vague message that doesn't reveal which field was wrong.
     */
    public InvalidCredentialsException() {
        super(
                "Invalid email or password",
                HttpStatus.UNAUTHORIZED,
                "INVALID_CREDENTIALS"
        );
    }
}