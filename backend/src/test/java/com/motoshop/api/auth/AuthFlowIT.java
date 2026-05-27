package com.motoshop.api.auth;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.motoshop.api.auth.dto.AuthResponse;
import com.motoshop.api.auth.dto.LoginRequest;
import com.motoshop.api.auth.dto.RegisterRequest;
import com.motoshop.api.support.PostgresIntegrationTest;

/**
 * End-to-end integration test of the Sprint 1 identity flow against a
 * real Postgres + Flyway + Spring context. Run with {@code mvn verify}.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthFlowIT extends PostgresIntegrationTest {

    @Autowired TestRestTemplate http;

    @Test
    @DisplayName("Buyer can register, log in and call /me; cannot create motorcycles")
    void buyerHappyPathAndAuthorisationBoundary() {
        // ---- register ----
        ResponseEntity<AuthResponse> registered = http.postForEntity(
                "/api/auth/register",
                new RegisterRequest("[email protected]", "buyerpass1", "Ivan Buyer"),
                AuthResponse.class);

        assertThat(registered.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(registered.getBody()).isNotNull();
        assertThat(registered.getBody().role()).isEqualTo(Role.BUYER);
        String buyerToken = registered.getBody().token();
        assertThat(buyerToken).isNotBlank();

        // ---- /me with the issued token ----
        ResponseEntity<Map> me = http.exchange(
                "/api/auth/me", HttpMethod.GET, bearer(buyerToken), Map.class);

        assertThat(me.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(me.getBody()).containsEntry("email", "[email protected]");
        assertThat(me.getBody()).containsEntry("role", "BUYER");

        // ---- buyer cannot create motorcycles ----
        ResponseEntity<String> forbidden = http.exchange(
                "/api/motorcycles", HttpMethod.POST,
                bearerWithBody(buyerToken, "{}"),
                String.class);

        assertThat(forbidden.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("Seeded admin can log in and reach admin-only endpoints")
    void adminCanLogIn() {
        ResponseEntity<AuthResponse> login = http.postForEntity(
                "/api/auth/login",
                new LoginRequest("[email protected]", "it-admin-pw"),
                AuthResponse.class);

        assertThat(login.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(login.getBody()).isNotNull();
        assertThat(login.getBody().role()).isEqualTo(Role.ADMIN);
    }

    @Test
    @DisplayName("Registering with a role hint never grants ADMIN")
    void publicRegistrationCannotEscalate() {
        // The DTO has no role field, so this raw JSON includes a stray
        // attribute that the server must ignore. Jackson will drop it by
        // default; the test makes the silent ignore explicit.
        String maliciousJson = """
                {
                  "email": "[email protected]",
                  "password": "wannabe1",
                  "fullName": "Mallory",
                  "role": "ADMIN"
                }
                """;
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<AuthResponse> resp = http.postForEntity(
                "/api/auth/register",
                new HttpEntity<>(maliciousJson, h),
                AuthResponse.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(resp.getBody().role()).isEqualTo(Role.BUYER);
    }

    @Test
    @DisplayName("Wrong password yields a generic 401 — no account enumeration")
    void wrongPasswordDoesNotLeakAccountExistence() {
        var existing = http.postForEntity("/api/auth/login",
                new LoginRequest("[email protected]", "totally-wrong"), Map.class);
        var nonExisting = http.postForEntity("/api/auth/login",
                new LoginRequest("[email protected]", "any"), Map.class);

        assertThat(existing.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(nonExisting.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        // Both responses must carry the same generic message.
        assertThat(existing.getBody().get("message"))
                .isEqualTo(nonExisting.getBody().get("message"))
                .isEqualTo("Invalid email or password");
    }

    // ---- helpers ----

    private static HttpEntity<Void> bearer(String token) {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(token);
        return new HttpEntity<>(h);
    }

    private static HttpEntity<String> bearerWithBody(String token, String body) {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(token);
        h.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, h);
    }
}