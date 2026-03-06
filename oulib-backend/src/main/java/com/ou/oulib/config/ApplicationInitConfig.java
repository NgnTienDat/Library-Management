package com.ou.oulib.config;


import com.ou.oulib.entity.User;
import com.ou.oulib.enums.UserRole;
import com.ou.oulib.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {
    PasswordEncoder passwordEncoder;

    @NonFinal
    @Value("${app.admin.password}")
    String adminPassword;

    @NonFinal
    @Value("${app.admin.email}")
    String adminEmail;

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        log.info("Initializing application.....");
        return args -> {
            if (!userRepository.existsByEmail(adminEmail)) {
                User admin = User.builder()
                        .role(UserRole.SYSADMIN)
                        .email(adminEmail)
                        .fullName("System Admin")
                        .password(passwordEncoder.encode(adminPassword))
                        .build();

                userRepository.save(admin);
                log.info("Created admin user");
            }
        };
    }
}
