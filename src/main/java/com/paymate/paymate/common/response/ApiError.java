package com.paymate.paymate.common.response;

import lombok.Builder;
import lombok.Getter;

/**
 * Structured error detail included in failed {@link ApiResponse} payloads.
 *
 * <p>The {@code code} field is a machine-readable constant the frontend
 * can use to handle errors programmatically (e.g. show specific UI).
 * The {@code details} field is a human-readable explanation.</p>
 *
 * <p>Example JSON output:</p>
 * <pre>
 * {
 *   "code": "INSUFFICIENT_FUNDS",
 *   "details": "Wallet balance of $5.00 is below transfer amount of $10.00"
 * }
 * </pre>
 *
 * @author Shubham Kumbhar
 */
@Getter
@Builder
public class ApiError {

    /**
     * Machine-readable error code in SCREAMING_SNAKE_CASE.
     * Frontend uses this to decide which error message to display.
     */
    private final String code;

    /**
     * Human-readable explanation of what went wrong.
     * Should be specific enough to help debugging without exposing internals.
     */
    private final String details;
}