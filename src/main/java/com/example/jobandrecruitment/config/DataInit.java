package com.example.jobandrecruitment.config;

import com.example.jobandrecruitment.model.entity.RoleUser;
import com.example.jobandrecruitment.model.entity.User;
import com.example.jobandrecruitment.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInit implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInit(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (userRepository.findByEmail("admin@example.com").isEmpty()) {
            User admin = User.builder()
                    .email("admin@example.com")
                    .password(passwordEncoder.encode("Admin@123456"))
                    .fullName("Administrator")
                    .role(RoleUser.ADMIN)
                    .isActive(true)
                    .build();
            userRepository.save(admin);
            System.out.println("  Email: admin@example.com");
            System.out.println("  Password: Admin@123456");
        } else {
            System.out.println("✓ Admin đã tồn tại");
        }
    }
}

