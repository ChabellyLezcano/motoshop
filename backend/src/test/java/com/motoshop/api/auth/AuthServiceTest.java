package com.motoshop.api.auth;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.motoshop.api.auth.dto.LoginRequest;
import com.motoshop.api.auth.dto.RegisterRequest;
import com.motoshop.api.security.AppUserDetails;
import com.motoshop.api.security.Role;
import com.motoshop.api.security.jwt.JwtProperties;
import com.motoshop.api.security.jwt.JwtService;
import com.motoshop.api.user.User;
import com.motoshop.api.user.UserRepository;

/**
 * Focused tests for {@link AuthService}. The repository is replaced by
 * a hand-rolled in-memory fake so we can assert on the persisted state
 * without mocking dozens of Spring Data methods.
 */
class AuthServiceTest {

    private InMemoryUserRepository users;
    private PasswordEncoder encoder;
    private AuthenticationManager authManager;
    private JwtService jwtService;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        users = new InMemoryUserRepository();
        encoder = new BCryptPasswordEncoder();
        authManager = mock(AuthenticationManager.class);
        jwtService = new JwtService(new JwtProperties(
                "this-is-a-test-only-secret-32bytes-min-aaaaaaaaaa", 60, "motoshop-api-test"));
        authService = new AuthService(users, encoder, authManager, jwtService);
    }

    @Test
    @DisplayName("Public registration always assigns BUYER, never ADMIN")
    void registrationForcesBuyerRole() {
        authService.register(new RegisterRequest("[email protected]", "irrelevant1", "Mallory"));

        User stored = users.findByEmail("[email protected]").orElseThrow();
        assertThat(stored.getRole()).isEqualTo(Role.BUYER);
    }

    @Test
    @DisplayName("Email is normalised to lower-case and trimmed")
    void emailNormalisation() {
        authService.register(new RegisterRequest("  [email protected]  ", "secret12", "Alice"));

        assertThat(users.findByEmail("[email protected]")).isPresent();
    }

    @Test
    @DisplayName("Duplicate email triggers EmailAlreadyUsedException")
    void duplicateEmailRejected() {
        authService.register(new RegisterRequest("[email protected]", "secret123", "Bob"));

        assertThatThrownBy(() ->
                authService.register(new RegisterRequest("[email protected]", "another9", "Bob 2")))
                .isInstanceOf(EmailAlreadyUsedException.class);
    }

    @Test
    @DisplayName("Password is stored as a BCrypt hash, not in clear text")
    void passwordIsHashed() {
        authService.register(new RegisterRequest("[email protected]", "plaintext1", "Carol"));

        User stored = users.findByEmail("[email protected]").orElseThrow();
        assertThat(stored.getPassword()).isNotEqualTo("plaintext1");
        assertThat(stored.getPassword()).startsWith("$2"); // BCrypt prefix
        assertThat(encoder.matches("plaintext1", stored.getPassword())).isTrue();
    }

    @Test
    @DisplayName("Successful login returns a parseable token with the right claims")
    void successfulLogin() {
        // Arrange: there is a user; the AuthenticationManager will say "yes".
        User existing = new User("[email protected]",
                encoder.encode("good-password"), "Dave", Role.BUYER);
        users.save(existing);

        Authentication authenticated = new UsernamePasswordAuthenticationToken(
                new AppUserDetails(existing), null,
                new AppUserDetails(existing).getAuthorities());
        when(authManager.authenticate(any())).thenReturn(authenticated);

        var response = authService.login(new LoginRequest("[email protected]", "good-password"));

        assertThat(response.token()).isNotBlank();
        assertThat(response.role()).isEqualTo(Role.BUYER);

        var parsed = jwtService.parse(response.token()).orElseThrow();
        assertThat(parsed.email()).isEqualTo("[email protected]");
        assertThat(parsed.role()).isEqualTo(Role.BUYER);
    }

    @Test
    @DisplayName("Login propagates BadCredentialsException from the AuthenticationManager")
    void loginPropagatesAuthFailure() {
        when(authManager.authenticate(any())).thenThrow(new BadCredentialsException("nope"));

        assertThatThrownBy(() ->
                authService.login(new LoginRequest("[email protected]", "wrong")))
                .isInstanceOf(BadCredentialsException.class);
    }

    // ----------------------------------------------------------------
    // Hand-rolled in-memory fake. Only implements what AuthService uses;
    // anything else throws UnsupportedOperationException so we notice
    // immediately if a future change starts relying on more methods.
    // ----------------------------------------------------------------
    private static class InMemoryUserRepository implements UserRepository {
        private final Map<Long, User> byId = new HashMap<>();
        private final AtomicLong sequence = new AtomicLong(1);

        @Override public Optional<User> findByEmail(String email) {
            return byId.values().stream().filter(u -> u.getEmail().equals(email)).findFirst();
        }
        @Override public boolean existsByEmail(String email) {
            return findByEmail(email).isPresent();
        }
        @Override public <S extends User> S save(S user) {
            if (user.getId() == null) {
                try {
                    var idField = User.class.getDeclaredField("id");
                    idField.setAccessible(true);
                    idField.set(user, sequence.getAndIncrement());
                    var createdField = User.class.getDeclaredField("createdAt");
                    createdField.setAccessible(true);
                    if (createdField.get(user) == null) {
                        createdField.set(user, java.time.Instant.now());
                    }
                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException(e);
                }
            }
            byId.put(user.getId(), user);
            return user;
        }
        @Override public Optional<User> findById(Long id) { return Optional.ofNullable(byId.get(id)); }
        // Methods we don't use in these tests:
        @Override public java.util.List<User> findAll() { throw new UnsupportedOperationException(); }
        @Override public java.util.List<User> findAll(org.springframework.data.domain.Sort sort) { throw new UnsupportedOperationException(); }
        @Override public org.springframework.data.domain.Page<User> findAll(org.springframework.data.domain.Pageable p) { throw new UnsupportedOperationException(); }
        @Override public java.util.List<User> findAllById(Iterable<Long> ids) { throw new UnsupportedOperationException(); }
        @Override public <S extends User> java.util.List<S> saveAll(Iterable<S> entities) { throw new UnsupportedOperationException(); }
        @Override public long count() { return byId.size(); }
        @Override public void deleteById(Long id) { byId.remove(id); }
        @Override public void delete(User u) { byId.remove(u.getId()); }
        @Override public void deleteAllById(Iterable<? extends Long> ids) { throw new UnsupportedOperationException(); }
        @Override public void deleteAll(Iterable<? extends User> entities) { throw new UnsupportedOperationException(); }
        @Override public void deleteAll() { byId.clear(); }
        @Override public boolean existsById(Long id) { return byId.containsKey(id); }
        @Override public void flush() { }
        @Override public <S extends User> S saveAndFlush(S e) { return save(e); }
        @Override public <S extends User> java.util.List<S> saveAllAndFlush(Iterable<S> entities) { throw new UnsupportedOperationException(); }
        @Override public void deleteAllInBatch(Iterable<User> entities) { throw new UnsupportedOperationException(); }
        @Override public void deleteAllByIdInBatch(Iterable<Long> ids) { throw new UnsupportedOperationException(); }
        @Override public void deleteAllInBatch() { byId.clear(); }
        @Override public User getOne(Long id) { return byId.get(id); }
        @Override public User getById(Long id) { return byId.get(id); }
        @Override public User getReferenceById(Long id) { return byId.get(id); }
        @Override public <S extends User> java.util.List<S> findAll(org.springframework.data.domain.Example<S> ex) { throw new UnsupportedOperationException(); }
        @Override public <S extends User> java.util.List<S> findAll(org.springframework.data.domain.Example<S> ex, org.springframework.data.domain.Sort s) { throw new UnsupportedOperationException(); }
        @Override public <S extends User> org.springframework.data.domain.Page<S> findAll(org.springframework.data.domain.Example<S> ex, org.springframework.data.domain.Pageable p) { throw new UnsupportedOperationException(); }
        @Override public <S extends User> long count(org.springframework.data.domain.Example<S> ex) { throw new UnsupportedOperationException(); }
        @Override public <S extends User> boolean exists(org.springframework.data.domain.Example<S> ex) { throw new UnsupportedOperationException(); }
        @Override public <S extends User, R> R findBy(org.springframework.data.domain.Example<S> ex, java.util.function.Function<org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery<S>, R> fn) { throw new UnsupportedOperationException(); }
        @Override public <S extends User> Optional<S> findOne(org.springframework.data.domain.Example<S> ex) { throw new UnsupportedOperationException(); }
    }
}