package com.motoshop.api.auth.dto;

import com.motoshop.api.user.Role;

/**
 * Response payload for successful login and registration.
 * Carries the JWT plus enough public user data for the client to
 * render the UI without an extra round trip.
 */
public record AuthResponse(
        String token,
        long expiresInSeconds,
        Long userId,
        String email,
        String fullName,
        Role role
) {
}