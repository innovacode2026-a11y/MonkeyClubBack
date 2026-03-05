package com.monkeyclub.gym.config;

import com.monkeyclub.gym.features.user.User;
import com.monkeyclub.gym.features.user.UserRepository;
import com.monkeyclub.gym.features.user.UserRole;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        createIfMissing("admin", "admin@monkeyclub.com", "admin123", UserRole.ADMIN);
        createIfMissing("recepcion", "recepcion@monkeyclub.com", "recepcion123", UserRole.RECEPCION);
        createIfMissing("cajero", "cajero@monkeyclub.com", "cajero123", UserRole.CAJERO);
    }

    private void createIfMissing(String username, String email, String password, UserRole role) {
        if (userRepository.existsByUsername(username)) {
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setActive(true);
        userRepository.save(user);
    }
}
