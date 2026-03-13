package com.example.e_learning.security;

import com.example.e_learning.model.Role;
import com.example.e_learning.model.User;
import com.example.e_learning.model.enums.UserRole;
import com.example.e_learning.repository.CategoryRepository;
import com.example.e_learning.repository.RoleRepository;
import com.example.e_learning.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Init Roles
        if (roleRepository.count() == 0) {
            roleRepository.save(new Role(null, UserRole.ROLE_STUDENT));
            roleRepository.save(new Role(null, UserRole.ROLE_INSTRUCTOR));
            roleRepository.save(new Role(null, UserRole.ROLE_ADMIN));
            System.out.println("Initialized roles: STUDENT, INSTRUCTOR, ADMIN");
        }

        // Init Admin User
        if (!userRepository.existsByEmail("admin@gmail.com")) {
            Role adminRole = roleRepository.findByName(UserRole.ROLE_ADMIN).get();
            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);

            User admin = User.builder()
                    .email("admin@gmail.com")
                    .password(passwordEncoder.encode("admin123"))
                    .fullName("System Admin")
                    .roles(roles)
                    .build();
            userRepository.save(admin);
            System.out.println("Created admin user: admin@gmail.com / admin123");
        }

        // Init Default Category
        if (categoryRepository.count() == 0) {
            categoryRepository.save(new com.example.e_learning.model.Category(null, "Programming"));
            categoryRepository.save(new com.example.e_learning.model.Category(null, "Design"));
            System.out.println("Initialized categories: Programming, Design");
        }
    }
}
