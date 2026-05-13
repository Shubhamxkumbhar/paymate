package com.paymate.paymate.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when attempting to create a resource that already exists.
 *
 * <p>Example: registering with an email that is already taken.
 * Maps to HTTP 409 Conflict.</p>
 *
 * @author Shubham Kumbhar
 */
public class DuplicateResourceException extends PayMateException {

    /**
     * Creates a conflict exception for a duplicate resource.
     *
     * @param resource the type of resource e.g. "User"
     * @param field    the field that is duplicate e.g. "email"
     * @param value    the duplicate value e.g. "john@example.com"
     */
    public DuplicateResourceException(String resource, String field, Object value) {
        super(
                String.format("%s already exists with %s: %s", resource, field, value),
                HttpStatus.CONFLICT,
                "DUPLICATE_RESOURCE"
        );
    }
}