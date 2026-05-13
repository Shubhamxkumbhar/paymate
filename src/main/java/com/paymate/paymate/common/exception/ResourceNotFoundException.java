package com.paymate.paymate.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when a requested resource does not exist in the database.
 *
 * <p>Examples: user not found by email, wallet not found by ID.
 * Maps to HTTP 404 Not Found via {@link PayMateException#getStatus()}.</p>
 *
 * @author Shubham Kumbhar
 */
public class ResourceNotFoundException extends PayMateException {

    /**
     * Creates a not-found exception for a specific resource.
     *
     * @param resource the type of resource not found e.g. "User"
     * @param field    the field that was searched e.g. "email"
     * @param value    the value that was not found e.g. "john@example.com"
     */
    public ResourceNotFoundException(String resource, String field, Object value) {
        super(
                // String.format builds the message: "User not found with email: john@example.com"
                String.format("%s not found with %s: %s", resource, field, value),
                HttpStatus.NOT_FOUND,
                "RESOURCE_NOT_FOUND"
        );
    }
}