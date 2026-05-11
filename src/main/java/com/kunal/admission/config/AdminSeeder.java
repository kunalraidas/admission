package com.kunal.admission.config;

import com.kunal.admission.datavalues.Role;
import com.kunal.admission.model.User;
import com.kunal.admission.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Bootstraps a default ADMIN user the first time the app starts.
 *
 * Without this, there is no way to log in as an admin (registration only
 * creates APPLICANT users). The default credentials are read from .env
 * (ADMIN_EMAIL, ADMIN_PASSWORD); if missing, sensible fallbacks are used.
 */
@Component
public class AdminSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${ADMIN_EMAIL:admin@bvicam.in}")
    private String adminEmail;

    @Value("${ADMIN_PASSWORD:admin123}")
    private String adminPassword;

    @Value("${ADMIN_NAME:Administrator}")
    private String adminName;

    public AdminSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.existsByEmail(adminEmail)) {
            log.info("Admin user already present ({}); skipping seed.", adminEmail);
            return;
        }
        User admin = new User(
                adminName,
                adminEmail,
                passwordEncoder.encode(adminPassword),
                Role.ADMIN
        );
        userRepository.save(admin);
        log.warn("Seeded default ADMIN user: {} (please change the password after first login)", adminEmail);
    }
}
