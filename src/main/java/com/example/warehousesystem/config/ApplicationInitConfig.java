package com.example.warehousesystem.config;

import com.example.warehousesystem.entity.User;
import com.example.warehousesystem.entity.User.Role;
import com.example.warehousesystem.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
    static final String ADMIN_USER_NAME = "admin";

    @NonFinal
    static final String ADMIN_PASSWORD = "admin";

    @Bean
    @ConditionalOnProperty(
            prefix = "spring",
            value = "datasource.driverClassName",
            havingValue = "com.mysql.cj.jdbc.Driver"
    )
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        log.info("Initializing application.....");
        return args -> {
            if (userRepository.findByUsername(ADMIN_USER_NAME).isEmpty()) {
                User user = User.builder()
                        .username(ADMIN_USER_NAME)
                        .fullName("Admin")
                        .passwordHash(passwordEncoder.encode(ADMIN_PASSWORD))
                        .role(Role.admin)
                        .isActive(true)
                        .build();

                userRepository.save(user);
                log.warn("Admin user has been created with default password: admin, please change it!");
            }
            log.info("Application initialization completed.");
        };
    }
}
