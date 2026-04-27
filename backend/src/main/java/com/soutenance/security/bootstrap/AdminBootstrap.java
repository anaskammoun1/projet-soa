package com.soutenance.security.bootstrap;

import com.soutenance.security.Role;
import com.soutenance.security.user.ApplicationUser;
import com.soutenance.security.user.ApplicationUserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class AdminBootstrap implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminBootstrap.class);

    private final ApplicationUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap.admin-username:admin}")
    private String adminUsername;

    @Value("${app.bootstrap.admin-email:admin@example.local}")
    private String adminEmail;

    @Value("${app.bootstrap.admin-password:}")
    private String adminPassword;

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.existsByRole(Role.ADMIN)) {
            return;
        }

        if (!StringUtils.hasText(adminPassword)) {
            LOGGER.warn("No ADMIN user exists and ADMIN_PASSWORD is not configured; skipping admin bootstrap.");
            return;
        }

        if (userRepository.existsByUsername(adminUsername)) {
            LOGGER.warn("Bootstrap username '{}' already exists without ADMIN role; skipping admin bootstrap.", adminUsername);
            return;
        }

        userRepository.save(ApplicationUser.builder()
                .username(adminUsername)
                .email(adminEmail)
                .passwordHash(passwordEncoder.encode(adminPassword))
                .role(Role.ADMIN)
                .enabled(true)
                .build());
        LOGGER.info("Seeded initial ADMIN user '{}'.", adminUsername);
    }
}
