package com.motoshop.api.security.jwt;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import com.motoshop.api.user.Role;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Issues and validates JWT access tokens (HS256).
 * <p>
 * The token carries the user id as subject, the email as a custom
 * claim and the role as another custom claim. We deliberately keep
 * the payload minimal: the resource server resolves the user from
 * the id on every request, so we never trust client-side state.
 */
@Service
public class JwtService {

    private static final String CLAIM_EMAIL = "email";
    private static final String CLAIM_ROLE  = "role";

    private final SecretKey key;
    private final long expirationMinutes;
    private final String issuer;

    public JwtService(JwtProperties props) {
        // jjwt requires at least 256 bits (32 bytes) for HS256.
        // We fail fast at startup if the configured secret is weaker.
        this.key = Keys.hmacShaKeyFor(props.secret().getBytes(StandardCharsets.UTF_8));
        this.expirationMinutes = props.expirationMinutes();
        this.issuer = props.issuer();
    }

    /** Builds a signed token for the given authenticated user. */
    public String issueToken(Long userId, String email, Role role) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(expirationMinutes, ChronoUnit.MINUTES);
        return Jwts.builder()
                .issuer(issuer)
                .subject(String.valueOf(userId))
                .claim(CLAIM_EMAIL, email)
                .claim(CLAIM_ROLE, role.name())
                .issuedAt(java.util.Date.from(now))
                .expiration(java.util.Date.from(expiresAt))
                .signWith(key)
                .compact();
    }

    /**
     * Parses and validates a token. Returns an empty optional if the
     * token is malformed, expired or signed with a different key.
     * Callers MUST treat an empty optional as an authentication failure.
     */
    public Optional<JwtPrincipal> parse(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .requireIssuer(issuer)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Long userId = Long.valueOf(claims.getSubject());
            String email = claims.get(CLAIM_EMAIL, String.class);
            Role role = Role.valueOf(claims.get(CLAIM_ROLE, String.class));
            return Optional.of(new JwtPrincipal(userId, email, role));
        } catch (JwtException | IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    /** Minimal projection of the trusted claims carried by a token. */
    public record JwtPrincipal(Long userId, String email, Role role) {
    }

    /** Exposed for response payloads ("expiresInSeconds"). */
    public long expirationSeconds() {
        return expirationMinutes * 60L;
    }
}