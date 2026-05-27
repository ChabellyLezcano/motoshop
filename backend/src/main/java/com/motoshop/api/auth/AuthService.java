package com.motoshop.api.auth;

import com.motoshop.api.auth.dto.AuthResponse;
import com.motoshop.api.auth.dto.LoginRequest;
import com.motoshop.api.auth.dto.RegisterRequest;
import com.motoshop.api.auth.exception.EmailAlreadyUsedException;
import com.motoshop.api.security.AppUserDetails;
import com.motoshop.api.security.jwt.JwtService;
import com.motoshop.api.user.Role;
import com.motoshop.api.user.User;
import com.motoshop.api.user.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;

  public AuthService(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      AuthenticationManager authenticationManager,
      JwtService jwtService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
  }

  /**
   * Registers a new buyer. The role is hard-coded to {@link Role#BUYER}: any role hint coming from
   * the client is irrelevant because the DTO does not even carry that field. This is the
   * application-level enforcement of RF-01 (least privilege on public sign-up).
   */
  @Transactional
  public AuthResponse register(RegisterRequest req) {
    String normalisedEmail = req.email().trim().toLowerCase();

    if (userRepository.existsByEmail(normalisedEmail)) {
      throw new EmailAlreadyUsedException(normalisedEmail);
    }

    User user =
        new User(
            normalisedEmail,
            passwordEncoder.encode(req.password()),
            req.fullName().trim(),
            Role.BUYER);
    user = userRepository.save(user);

    String token = jwtService.issueToken(user.getId(), user.getEmail(), user.getRole());
    return new AuthResponse(
        token,
        jwtService.expirationSeconds(),
        user.getId(),
        user.getEmail(),
        user.getFullName(),
        user.getRole());
  }

  /**
   * Authenticates an existing user. We delegate credential checking to Spring's {@link
   * AuthenticationManager}, which goes through the configured {@code DaoAuthenticationProvider} and
   * {@code BCryptPasswordEncoder}. Failed authentication bubbles up as a {@code
   * BadCredentialsException} and is mapped to HTTP 401 by {@link
   * com.motoshop.api.web.GlobalExceptionHandler}.
   */
  public AuthResponse login(LoginRequest req) {
    String normalisedEmail = req.email().trim().toLowerCase();

    Authentication auth =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(normalisedEmail, req.password()));

    AppUserDetails principal = (AppUserDetails) auth.getPrincipal();
    String token =
        jwtService.issueToken(principal.getId(), principal.getUsername(), principal.getRole());

    // Reload only the name. Avoids exposing the entity but keeps the
    // response shape consistent with /register and /me.
    String fullName = userRepository.findById(principal.getId()).map(User::getFullName).orElse("");

    return new AuthResponse(
        token,
        jwtService.expirationSeconds(),
        principal.getId(),
        principal.getUsername(),
        fullName,
        principal.getRole());
  }
}
