package ru.kata.spring.boot_security.demo.util;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository, UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Role roleUser = roleRepository.findByName("ROLE_USER");
        Role roleAdmin = roleRepository.findByName("ROLE_ADMIN");

        if (roleUser == null) {
            roleUser = new Role("ROLE_USER");
            roleRepository.save(roleUser);
        }
        if (roleAdmin == null) {
            roleAdmin = new Role("ROLE_ADMIN");
            roleRepository.save(roleAdmin);
        }

        if (userRepository.findByEmail("admin@example.com").isEmpty()) {
            User admin = new User();
            admin.setFirstName("admin");
            admin.setLastName("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setEmail("admin@example.com");
            admin.setAge(35);
            admin.setRoles(Set.of(roleAdmin, roleUser));
            userRepository.save(admin);
        }

        if (userRepository.findByEmail("user@example.com").isEmpty()) {
            User user = new User();
            user.setFirstName("user");
            user.setLastName("user");
            user.setPassword(passwordEncoder.encode("user"));
            user.setEmail("user@example.com");
            user.setAge(35);
            user.setRoles(Set.of(roleUser));
            userRepository.save(user);
        }
    }
}
