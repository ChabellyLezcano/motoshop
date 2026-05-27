package com.motoshop.api.auth;

/**
 * Thrown when a registration attempt collides with an existing email.
 * Mapped to HTTP 409 Conflict by the global exception handler.
 */
public class EmailAlreadyUsedException extends RuntimeException {

    public EmailAlreadyUsedException(String email) {
        super("Email already registered: " + email);
    }
}