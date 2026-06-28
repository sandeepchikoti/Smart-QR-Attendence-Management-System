package com.university.attendance.config;

import com.university.attendance.user.entity.Role;
import com.university.attendance.user.entity.User;
import com.university.attendance.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            log.info("Database users table is empty. Seeding default roles...");

            User admin = User.builder()
                    .email("admin@university.edu")
                    .passwordHash(passwordEncoder.encode("Admin123!"))
                    .firstName("Institutional")
                    .lastName("Administrator")
                    .role(Role.ADMIN)
                    .isActive(true)
                    .build();
            userRepository.save(admin);

            User faculty = User.builder()
                    .email("faculty@university.edu")
                    .passwordHash(passwordEncoder.encode("Faculty123!"))
                    .firstName("Professor")
                    .lastName("John")
                    .role(Role.FACULTY)
                    .isActive(true)
                    .build();
            userRepository.save(faculty);

            User student = User.builder()
                    .email("student@university.edu")
                    .passwordHash(passwordEncoder.encode("Student123!"))
                    .firstName("Student")
                    .lastName("Alex")
                    .role(Role.STUDENT)
                    .isActive(true)
                    .build();
            userRepository.save(student);

            log.info("Default users seeded: admin@university.edu, faculty@university.edu, student@university.edu");
        } else {
            log.info("Database users already exist. Skipping seeder.");
        }
    }
}
