package com.motoshop.api.security.jwt;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Strongly-typed binding of the {@code app.jwt.*} configuration block. The secret must be at least
 * 32 bytes long because the HS256 signer used by jjwt rejects shorter keys.
 */
@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(
    @NotBlank String secret, @Min(1) long expirationMinutes, @NotBlank String issuer) {}
