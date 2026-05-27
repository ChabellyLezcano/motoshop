package com.motoshop.api.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.motoshop.api.user.Role;
import com.motoshop.api.user.User;
import com.motoshop.api.user.UserRepository;

/**
 * Idempotently seeds the first ADMIN user on startup, from credentials
 * supplied via environment variables (see {@link AdminProperties}).
 * <p>
 * This is the only path that creates an ADMIN: the public registration
 * endpoint is hard-wired to BUYER. Subsequent ADMIN promotions will be
 * available in Sprint 2 through a protected back-office endpoint.
 * <p>
 * The seeder NEVER overwrites an existing user, so it is safe to redeploy
 * with the same configuration. Rotating the admin password requires a
 * different operational procedure (manual update or a dedicated tool).
 */
@Component
public class InitialAdminSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(InitialAdminSeeder.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminProperties adminProperties;

    public InitialAdminSeeder(UserRepository userRepository,
                              PasswordEncoder passwordEncoder,
                              AdminProperties adminProperties) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminProperties = adminProperties;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        String email = adminProperties.email().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            log.info("Initial admin '{}' already present, no action taken", email);
            return;
        }

        User admin = new User(
                email,
                passwordEncoder.encode(adminProperties.password()),
                "Administrator",
                Role.ADMIN
        );
        userRepository.save(admin);
        log.info("Initial admin '{}' created on first boot", email);
    }
}