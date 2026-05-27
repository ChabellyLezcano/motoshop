package com.motoshop.api.support;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Base class for integration tests that need a real Postgres instance.
 * <p>
 * The container is started ONCE per JVM (singleton pattern via static
 * field) instead of once per test class, which keeps the suite under a
 * couple of seconds of overhead. Spring's
 * {@link DynamicPropertySource} wires the random container port into
 * {@code spring.datasource.*} so the application uses the same database
 * the tests provisioned.
 * <p>
 * Flyway runs against this Postgres as it would in production, which is
 * the entire point of this profile: we want to catch a broken migration
 * here, not in staging.
 */
@Testcontainers
@ActiveProfiles("integration-test")
public abstract class PostgresIntegrationTest {

    @SuppressWarnings("resource")
    static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("motoshop")
                    .withUsername("motoshop")
                    .withPassword("motoshop")
                    .withReuse(true);

    static {
        POSTGRES.start();
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url",      POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        // Flyway re-enabled here even if the base application.yml had it off.
        registry.add("spring.flyway.enabled", () -> "true");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
    }
}