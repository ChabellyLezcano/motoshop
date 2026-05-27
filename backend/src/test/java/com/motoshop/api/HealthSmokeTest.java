package com.motoshop.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test trivial del Sprint 0. Su unico proposito es que la pipeline
 * de CI tenga algo verde que ejecutar. No carga el contexto de Spring
 * para no requerir una base de datos durante la build de GitHub Actions.
 */
class HealthSmokeTest {

    @Test
    void contextStringsAreConsistent() {
        String service = "motoshop-api";
        assertEquals("motoshop-api", service);
    }
}
