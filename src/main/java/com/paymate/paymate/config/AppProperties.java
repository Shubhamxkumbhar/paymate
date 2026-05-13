package com.paymate.paymate.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Strongly-typed configuration properties for PayMate application metadata.
 *
 * <p>Binds to the {@code app.*} namespace in {@code application.yml}.
 * Prefer this over {@code @Value} annotations for grouped configuration —
 * it is refactor-safe, testable, and validated at startup.</p>
 *
 * <p>Example yml binding:</p>
 * <pre>
 * app:
 *   name: PayMate
 *   version: 1.0.0
 * </pre>
 *
 * @author Shubham Kumbhar
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    /**
     * Human-readable name of the application.
     * Displayed in health checks and API responses.
     */
    private String name;

    /**
     * Current version of the application.
     * Should match the version in {@code pom.xml}.
     */
    private String version;
}