package com.motoshop.api.auth;

import com.motoshop.api.auth.dto.AuthResponse;
import com.motoshop.api.auth.dto.LoginRequest;
import com.motoshop.api.auth.dto.RegisterRequest;
import com.motoshop.api.security.jwt.JwtService.JwtPrincipal;
import com.motoshop.api.user.User;
import com.motoshop.api.user.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Sign-up, sign-in and current user info")
public class AuthController {

  private final AuthService authService;
  private final UserRepository userRepository;

  public AuthController(AuthService authService, UserRepository userRepository) {
    this.authService = authService;
    this.userRepository = userRepository;
  }

  @Operation(
      summary = "Register a new buyer",
      description =
          """
                    Creates a new user with role BUYER and returns a JWT.
                    The role is forced server-side regardless of any value sent
                    by the client; the request DTO does not even carry that field.
                    """)
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "User registered, token issued"),
    @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
    @ApiResponse(responseCode = "409", description = "Email already registered", content = @Content)
  })
  @PostMapping("/register")
  public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
    return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(req));
  }

  @Operation(
      summary = "Authenticate an existing user",
      description =
          "Returns a JWT to be sent as `Authorization: Bearer <token>` on protected endpoints.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Login successful, token issued"),
    @ApiResponse(
        responseCode = "401",
        description = "Invalid email or password",
        content = @Content)
  })
  @PostMapping("/login")
  public AuthResponse login(@Valid @RequestBody LoginRequest req) {
    return authService.login(req);
  }

  @Operation(
      summary = "Get the currently authenticated user",
      description = "Used by the SPA to rehydrate its session after a page reload.",
      security = @SecurityRequirement(name = "bearer-jwt"))
  @ApiResponse(
      responseCode = "200",
      content = @Content(schema = @Schema(implementation = MeResponse.class)))
  @GetMapping("/me")
  public Map<String, Object> me(@AuthenticationPrincipal JwtPrincipal principal) {
    User user = userRepository.findById(principal.userId()).orElseThrow();
    return Map.of(
        "userId", user.getId(),
        "email", user.getEmail(),
        "fullName", user.getFullName(),
        "role", user.getRole());
  }

  /** Schema-only record so springdoc renders /me with a typed example. */
  @Schema(name = "MeResponse")
  private record MeResponse(Long userId, String email, String fullName, String role) {}
}
