package com.motoshop.api.catalog;

/**
 * Thrown when a motorcycle id is not present in the catalog.
 * Mapped to HTTP 404 by the global exception handler.
 */
public class MotorcycleNotFoundException extends RuntimeException {

    public MotorcycleNotFoundException(Long id) {
        super("Motorcycle not found: " + id);
    }
}