package com.motoshop.api.security.jwt;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.motoshop.api.user.Role;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Authenticates each request from the {@code Authorization: Bearer ...}
 * header. The filter is intentionally permissive: invalid or missing
 * tokens are not rejected here — they simply leave the security context
 * empty, and {@link org.springframework.security.web.access.ExceptionTranslationFilter}
 * decides afterwards whether the target endpoint required authentication.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String HEADER = "Authorization";
    private static final String PREFIX = "Bearer ";

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain) throws ServletException, IOException {

        extractToken(request)
                .flatMap(jwtService::parse)
                .ifPresent(principal -> authenticate(principal, request));

        chain.doFilter(request, response);
    }

    private Optional<String> extractToken(HttpServletRequest request) {
        String header = request.getHeader(HEADER);
        if (!StringUtils.hasText(header) || !header.startsWith(PREFIX)) {
            return Optional.empty();
        }
        return Optional.of(header.substring(PREFIX.length()).trim());
    }

    private void authenticate(JwtService.JwtPrincipal principal, HttpServletRequest request) {
        // We DO NOT reload the user from the database on every request.
        // The trust boundary is the JWT signature; if we needed to revoke
        // tokens immediately we would introduce a token blocklist or
        // shorter expirations in a later sprint.
        Role role = principal.role();
        List<SimpleGrantedAuthority> authorities =
                List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(principal, null, authorities);
        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}