package com.motoshop.api.auth;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.motoshop.api.auth.dto.AuthResponse;
import com.motoshop.api.auth.dto.LoginRequest;
import com.motoshop.api.auth.dto.RegisterRequest;
import com.motoshop.api.auth.exception.EmailAlreadyUsedException;
import com.motoshop.api.config.GlobalExceptionHandler;
import com.motoshop.api.security.SecurityConfig;
import com.motoshop.api.security.jwt.JwtService;
import com.motoshop.api.user.Role;
import com.motoshop.api.user.UserRepository;

@WebMvcTest(controllers = AuthController.class)
@Import({
  SecurityConfig.class,
  GlobalExceptionHandler.class,
  AuthControllerWebTest.WebTestConfig.class
})
@ActiveProfiles("test")
class AuthControllerWebTest {

  @Autowired MockMvc mvc;
  @Autowired ObjectMapper json;

  @MockBean AuthService authService;
  @MockBean UserRepository userRepository;
  @MockBean JwtService jwtService;
  @MockBean AuthenticationManager authenticationManager;
  @MockBean UserDetailsService userDetailsService;
  
  // Mocks de seguridad para el contexto de filtro
  @MockBean com.motoshop.api.security.RestAuthenticationEntryPoint restAuthenticationEntryPoint;
  @MockBean com.motoshop.api.security.RestAccessDeniedHandler restAccessDeniedHandler;

  @Test
  void registerReturns201AndBody() throws Exception {
    var req = new RegisterRequest("alice@example.com", "secret123", "Alice");
    var resp = new AuthResponse("tok", 3600, 1L, "alice@example.com", "Alice", Role.BUYER);
    when(authService.register(any())).thenReturn(resp);

    mvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(req)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.token").value("tok"))
        .andExpect(jsonPath("$.role").value("BUYER"));
  }

  @Test
  void registerWithInvalidEmailReturns400() throws Exception {
    var bad = new RegisterRequest("not-an-email", "secret123", "Alice");

    mvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(bad)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Validation failed"))
        .andExpect(jsonPath("$.fieldErrors.email").exists());
  }

  @Test
  void registerWithShortPasswordReturns400() throws Exception {
    var bad = new RegisterRequest("bob@example.com", "short", "Alice");

    mvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(bad)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.fieldErrors.password").exists());
  }

  @Test
  void duplicateEmailReturns409() throws Exception {
    var req = new RegisterRequest("duplicate@example.com", "secret123", "Alice");
    when(authService.register(any())).thenThrow(new EmailAlreadyUsedException("duplicate@example.com"));

    mvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(req)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.error").value("Conflict"));
  }

  @Test
  void loginWithWrongPasswordReturns401WithGenericMessage() throws Exception {
    var req = new LoginRequest("user@example.com", "wrong");
    when(authService.login(any())).thenThrow(new BadCredentialsException("nope"));

    mvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(req)))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.message").value("Invalid email or password"));
  }

  @TestConfiguration
  static class WebTestConfig {}
}