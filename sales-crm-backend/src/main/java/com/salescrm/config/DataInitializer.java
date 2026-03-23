package com.salescrm.config;

import com.salescrm.entity.User;
import com.salescrm.enums.RoleType;
import com.salescrm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Seed admin user if not exists
        if (!userRepository.existsByEmail("admin@salescrm.com")) {
            User admin = User.builder()
                    .name("System Admin")
                    .email("admin@salescrm.com")
                    .password(passwordEncoder.encode("admin123"))
                    .phone("9999999999")
                    .role(RoleType.ROLE_ADMIN)
                    .active(true)
                    .build();
            userRepository.save(admin);
            System.out.println("✅ Default Admin created: admin@salescrm.com / admin123");
        }

        // Seed a sample manager
        if (!userRepository.existsByEmail("manager@salescrm.com")) {
            User manager = User.builder()
                    .name("Sales Manager")
                    .email("manager@salescrm.com")
                    .password(passwordEncoder.encode("manager123"))
                    .phone("8888888888")
                    .role(RoleType.ROLE_SALES_MANAGER)
                    .active(true)
                    .build();
            userRepository.save(manager);
            System.out.println("✅ Default Manager created: manager@salescrm.com / manager123");
        }

        // Seed a sample employee
        if (!userRepository.existsByEmail("employee@salescrm.com")) {
            User employee = User.builder()
                    .name("Sales Employee")
                    .email("employee@salescrm.com")
                    .password(passwordEncoder.encode("employee123"))
                    .phone("7777777777")
                    .role(RoleType.ROLE_SALES_EMPLOYEE)
                    .active(true)
                    .build();
            userRepository.save(employee);
            System.out.println("✅ Default Employee created: employee@salescrm.com / employee123");
        }
    }
}
