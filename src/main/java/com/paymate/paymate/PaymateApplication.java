package com.paymate.paymate;

import com.paymate.paymate.config.AppProperties;
import com.paymate.paymate.config.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Entry point for the PayMate payment platform application.
 *
 * <p>Bootstraps the Spring context, starts embedded Tomcat,
 * and initialises all configured beans and modules.</p>
 *
 * <p>Excludes {@link UserDetailsServiceAutoConfiguration} because
 * PayMate provides its own security configuration via
 * {@link com.paymate.paymate.config.SecurityConfig}. Without this
 * exclusion Spring Boot generates a random default password.</p>
 *
 * @author PayMate Engineering
 */
@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
@EnableConfigurationProperties({AppProperties.class, JwtProperties.class})
public class PaymateApplication {

	/**
	 * Application entry point.
	 *
	 * @param args command-line arguments passed to the JVM
	 */
	public static void main(String[] args) {
		SpringApplication.run(PaymateApplication.class, args);
	}
}