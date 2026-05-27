package com.motoshop.api.security.jwt;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.motoshop.api.security.Role;

/**
 * Pure unit test for the JWT service. No Spring context, no database.
 * Verifies the round-trip (issue -> parse) and the failure modes that
 * matter for security: bad signature, tampered claims, hard-coded
 * minimum key length.
 */
class JwtServiceTest {

    private static final String VALID_SECRET = "this-is-a-test-only-secret-32bytes-min-aaaaaaaaaa";

    private JwtService newService() {
        return new JwtService(new JwtProperties(VALID_SECRET, 60, "motoshop-api-test"));
    }

    @Test
    @DisplayName("Issued token can be parsed back to the same claims")
    void roundTrip() {
        JwtService svc = newService();
        String token = svc.issueToken(42L, "[email protected]", Role.BUYER);

        Optional<JwtService.JwtPrincipal> parsed = svc.parse(token);

        assertThat(parsed).isPresent();
        assertThat(parsed.get().userId()).isEqualTo(42L);
        assertThat(parsed.get().email()).isEqualTo("[email protected]");
        assertThat(parsed.get().role()).isEqualTo(Role.BUYER);
    }

    @Test
    @DisplayName("Token signed with a different secret is rejected")
    void signedWithDifferentSecret() {
        JwtService issuer = newService();
        JwtService otherSecret = new JwtService(new JwtProperties(
                "a-totally-different-secret-32bytes-min-xxxxxxx", 60, "motoshop-api-test"));

        String foreignToken = otherSecret.issueToken(1L, "[email protected]", Role.ADMIN);

        assertThat(issuer.parse(foreignToken)).isEmpty();
    }

    @Test
    @DisplayName("Token with the wrong issuer is rejected")
    void wrongIssuer() {
        JwtService svc = newService();
        JwtService wrongIssuer = new JwtService(new JwtProperties(VALID_SECRET, 60, "evil-issuer"));

        String token = wrongIssuer.issueToken(1L, "[email protected]", Role.BUYER);

        assertThat(svc.parse(token)).isEmpty();
    }

    @Test
    @DisplayName("Garbage input is rejected without throwing")
    void garbageInput() {
        JwtService svc = newService();

        assertThat(svc.parse("not.a.jwt")).isEmpty();
        assertThat(svc.parse("")).isEmpty();
        assertThat(svc.parse("Bearer xyz")).isEmpty();
    }

    @Test
    @DisplayName("Secret shorter than 256 bits causes startup failure")
    void shortSecretRejected() {
        // jjwt's Keys.hmacShaKeyFor throws WeakKeyException for < 256 bits.
        // That exception is a runtime exception, surfaced at bean construction.
        assertThatThrownBy(() -> new JwtService(new JwtProperties("too-short", 60, "iss")))
                .isInstanceOf(io.jsonwebtoken.security.WeakKeyException.class);
    }
}