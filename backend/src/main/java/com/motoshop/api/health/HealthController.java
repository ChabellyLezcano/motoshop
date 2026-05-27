package com.motoshop.api.health;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

/**
 * Endpoint de salud del Sprint 0.
 * Confirma que la API esta viva y responde. En sprints posteriores
 * la observabilidad se apoyara en /actuator/health y /actuator/prometheus.
 */
@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "status", "UP",
                "service", "motoshop-api",
                "timestamp", Instant.now().toString()
        );
    }
}
