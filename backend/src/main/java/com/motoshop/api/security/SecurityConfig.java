package com.motoshop.api.security;

import com.motoshop.api.security.jwt.JwtAuthenticationFilter;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Central security configuration.
 *
 * <ul>
 *   <li>Stateless session policy: every request authenticates itself with a Bearer JWT, no
 *       server-side session state.
 *   <li>CSRF disabled: there are no cookies; the token must be attached explicitly by the client,
 *       so CSRF does not apply.
 *   <li>{@link JwtAuthenticationFilter} runs before {@link UsernamePasswordAuthenticationFilter}.
 *   <li>Authorisation by role uses Spring's {@code hasRole(...)}, which maps to authority {@code
 *       ROLE_<NAME>}.
 * </ul>
 *
 * Method-level security ({@code @PreAuthorize}) is enabled so future sprints can secure individual
 * service methods.
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

  @Value("${app.cors.allowed-origins}")
  private String allowedOriginsRaw;

  @Bean
  public PasswordEncoder passwordEncoder() {
    // BCrypt with default strength (10). Adequate for this project;
    // upgrading the cost factor later does not break existing hashes,
    // since BCrypt encodes the cost in the hash prefix.
    return new BCryptPasswordEncoder();
  }

  @Bean
  public DaoAuthenticationProvider authenticationProvider(
      UserDetailsService uds, PasswordEncoder encoder) {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(uds);
    provider.setPasswordEncoder(encoder);
    return provider;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg)
      throws Exception {
    return cfg.getAuthenticationManager();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration cors = new CorsConfiguration();
    cors.setAllowedOrigins(Arrays.asList(allowedOriginsRaw.split("\\s*,\\s*")));
    cors.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    cors.setAllowedHeaders(List.of("*"));
    cors.setExposedHeaders(List.of("Authorization"));
    cors.setAllowCredentials(true);
    cors.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/api/**", cors);
    return source;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(
      HttpSecurity http,
      JwtAuthenticationFilter jwtFilter,
      RestAuthenticationEntryPoint authEntryPoint,
      RestAccessDeniedHandler accessDeniedHandler)
      throws Exception {

    http.cors(c -> c.configurationSource(corsConfigurationSource()))
        .csrf(csrf -> csrf.disable())
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .exceptionHandling(
            eh ->
                eh.authenticationEntryPoint(authEntryPoint)
                    .accessDeniedHandler(accessDeniedHandler))
        .authorizeHttpRequests(
            auth ->
                auth
                    // OpenAPI / Swagger UI (public, no auth)
                    .requestMatchers(
                        "/v3/api-docs", "/v3/api-docs/**",
                        "/swagger-ui.html", "/swagger-ui/**")
                    .permitAll()
                    // Public health and observability endpoints
                    .requestMatchers("/api/health", "/actuator/**")
                    .permitAll()
                    // Public auth endpoints (login, register)
                    .requestMatchers("/api/auth/login", "/api/auth/register")
                    .permitAll()
                    // Public read access to the catalog
                    .requestMatchers(HttpMethod.GET, "/api/motorcycles", "/api/motorcycles/**")
                    .permitAll()
                    // CORS preflight
                    .requestMatchers(HttpMethod.OPTIONS, "/**")
                    .permitAll()
                    // Admin-only writes on the catalog
                    .requestMatchers(HttpMethod.POST, "/api/motorcycles/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/api/motorcycles/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PATCH, "/api/motorcycles/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/api/motorcycles/**")
                    .hasRole("ADMIN")
                    // Everything else requires authentication
                    .anyRequest()
                    .authenticated())
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
