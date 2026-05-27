package com.motoshop.api.web;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.motoshop.api.auth.EmailAlreadyUsedException;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Translates uncaught exceptions to JSON error responses with a stable
 * shape: {@code status, error, message, path, timestamp, [fieldErrors]}.
 * The 401/403 paths are handled directly by Spring Security's entry
 * points; this advice covers everything else that escapes a controller.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex,
                                                                HttpServletRequest req) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fe ->
                fieldErrors.put(fe.getField(), fe.getDefaultMessage()));

        return body(HttpStatus.BAD_REQUEST, "Validation failed",
                "One or more fields are invalid", req, fieldErrors);
    }

    @ExceptionHandler(EmailAlreadyUsedException.class)
    public ResponseEntity<Map<String, Object>> handleEmailConflict(EmailAlreadyUsedException ex,
                                                                   HttpServletRequest req) {
        return body(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), req, null);
    }

    /**
     * Both bad password and unknown user must answer with the SAME error,
     * so callers cannot tell whether the email exists. This is a small
     * but important hardening against account enumeration.
     */
    @ExceptionHandler({ BadCredentialsException.class, UsernameNotFoundException.class })
    public ResponseEntity<Map<String, Object>> handleBadCredentials(Exception ex,
                                                                    HttpServletRequest req) {
        return body(HttpStatus.UNAUTHORIZED, "Unauthorized",
                "Invalid email or password", req, null);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex,
                                                                     HttpServletRequest req) {
        return body(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), req, null);
    }

    private ResponseEntity<Map<String, Object>> body(HttpStatus status,
                                                     String error,
                                                     String message,
                                                     HttpServletRequest req,
                                                     Map<String, String> fieldErrors) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("status",    status.value());
        payload.put("error",     error);
        payload.put("message",   message);
        payload.put("path",      req.getRequestURI());
        payload.put("timestamp", Instant.now().toString());
        if (fieldErrors != null && !fieldErrors.isEmpty()) {
            payload.put("fieldErrors", fieldErrors);
        }
        return ResponseEntity.status(status).body(payload);
    }

    @ExceptionHandler(com.motoshop.api.catalog.MotorcycleNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleMotorcycleNotFound(
            com.motoshop.api.catalog.MotorcycleNotFoundException ex,
            HttpServletRequest req) {
        return body(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), req, null);
    }
}