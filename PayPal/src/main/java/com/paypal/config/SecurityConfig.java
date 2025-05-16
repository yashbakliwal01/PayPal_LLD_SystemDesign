package com.paypal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // (1) Disable CSRF if you are using only stateless APIs (optional for testing):
        http.csrf(AbstractHttpConfigurer::disable);
        // (2) Configure access rules:
        http.authorizeHttpRequests(auth -> auth
            .anyRequest().permitAll()  // allow all requests (disable auth)
        );
        return http.build();
    }
}
