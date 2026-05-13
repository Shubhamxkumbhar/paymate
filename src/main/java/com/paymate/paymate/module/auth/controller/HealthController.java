package com.paymate.paymate.module.auth.controller;

import com.paymate.paymate.common.response.ApiResponse;
import com.paymate.paymate.config.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

/**
 * REST controller providing system health information.
 *
 * <p>Used by load balancers, monitoring systems, and developers to confirm
 * the service is running and all critical dependencies are reachable.
 * Returns HTTP 200 when healthy, HTTP 503 when any dependency is down.</p>
 *
 * <p>This endpoint is intentionally unauthenticated — monitoring tools
 * must be able to reach it without credentials.</p>
 *
 * @author Shubham Kumbhar
 */
@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class HealthController {

    /**
     * Used to verify database connectivity via a lightweight ping query.
     */
    private final JdbcTemplate jdbcTemplate;

    /**
     * Application metadata properties bound from {@code application.yml}.
     * Injected via constructor by {@link RequiredArgsConstructor}.
     */
    private final AppProperties appProperties;

    /**
     * Returns the current health status of the PayMate service.
     *
     * <p>Checks database connectivity by executing a lightweight
     * {@code SELECT 1} query. Returns HTTP 503 if the database
     * is unreachable so load balancers can remove this instance
     * from rotation automatically.</p>
     *
     * @return HTTP 200 with database UP, or HTTP 503 if database is DOWN
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, String>>> health() {
        log.debug("Health check requested");

        String dbStatus = checkDatabaseConnectivity();

        Map<String, String> healthData = Map.of(
                "service", appProperties.getName(),
                "version", appProperties.getVersion(),
                "database", dbStatus,
                "timestamp", Instant.now().toString()
        );

        if ("UP".equals(dbStatus)) {
            return ResponseEntity.ok(
                    ApiResponse.success("Service is healthy", healthData)
            );
        }

        log.error("Health check failed — database is unreachable");
        return ResponseEntity
                .status(503)
                .body(ApiResponse.error("Service is unhealthy", null));
    }

    /**
     * Pings the database with a minimal query to verify connectivity.
     *
     * <p>{@code SELECT 1} does no table scans and returns instantly,
     * making it the lightest possible connectivity check.</p>
     *
     * @return {@code "UP"} if database responded, {@code "DOWN"} otherwise
     */
    private String checkDatabaseConnectivity() {
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return "UP";
        } catch (Exception ex) {
            log.error("Database connectivity check failed: {}", ex.getMessage());
            return "DOWN";
        }
    }
}