package com.motoshop.api.bootstrap;

import org.springframework.boot.context.properties.ConfigurationProperties;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Strongly-typed binding of the {@code app.admin.*} properties. These
 * credentials seed the very first ADMIN user at startup. They MUST be
 * overridden in production via {@code APP_ADMIN_EMAIL} and
 * {@code APP_ADMIN_PASSWORD} environment variables.
 */
@ConfigurationProperties(prefix = "app.admin")
public record AdminProperties(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8) String password
) {
}