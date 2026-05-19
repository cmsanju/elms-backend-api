package com.elms.config;

import com.elms.model.User;
import com.elms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            log.info("Initializing demo users...");

            userRepository.save(User.builder()
                .fullName("Admin User")
                .email("admin@elms.com")
                .password(passwordEncoder.encode("demo123"))
                .role(User.Role.ADMIN)
                .skills("Administration, System Management")
                .active(true)
                .build());

            userRepository.save(User.builder()
                .fullName("Dr. Ravi Kumar")
                .email("instructor@elms.com")
                .password(passwordEncoder.encode("demo123"))
                .role(User.Role.INSTRUCTOR)
                .skills("Java, Spring Boot, Angular, Microservices")
                .bio("Senior Software Architect with 15+ years of experience")
                .active(true)
                .build());

            userRepository.save(User.builder()
                .fullName("Arjun Sharma")
                .email("student@elms.com")
                .password(passwordEncoder.encode("demo123"))
                .role(User.Role.STUDENT)
                .skills("Java, Python, Angular, Spring Boot")
                .bio("Full Stack Developer in progress")
                .active(true)
                .build());

            log.info("✅ Demo users created:");
            log.info("  Admin:      admin@elms.com / demo123");
            log.info("  Instructor: instructor@elms.com / demo123");
            log.info("  Student:    student@elms.com / demo123");
        }
    }
}
