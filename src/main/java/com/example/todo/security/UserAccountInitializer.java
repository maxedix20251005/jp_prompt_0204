package com.example.todo.security;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.todo.entity.UserAccount;
import com.example.todo.repository.UserAccountRepository;

@Configuration
public class UserAccountInitializer {

    @Bean
    public ApplicationRunner initUsers(UserAccountRepository userAccountRepository,
                                       PasswordEncoder passwordEncoder) {
        return args -> {
            String username = "admin";
            if (userAccountRepository.findByUsername(username).isEmpty()) {
                UserAccount user = UserAccount.builder()
                        .username(username)
                        .password(passwordEncoder.encode("password"))
                        .role("ROLE_USER")
                        .build();
                userAccountRepository.save(user);
            }
            String demoUsername = "max";
            if (userAccountRepository.findByUsername(demoUsername).isEmpty()) {
                UserAccount user = UserAccount.builder()
                        .username(demoUsername)
                        .password(passwordEncoder.encode("password"))
                        .role("ROLE_USER")
                        .build();
                userAccountRepository.save(user);
            }
        };
    }
}
