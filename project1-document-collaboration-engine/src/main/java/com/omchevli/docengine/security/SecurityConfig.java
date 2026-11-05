package com.omchevli.docengine.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Demo security setup: HTTP Basic with two in-memory users.
 * om / om-password (EDITOR) and guest / guest-password (VIEWER).
 * Only EDITORs can create or modify documents; both roles can read documents
 * they own or that were shared with them (enforced per-document in the service).
 * In production this is where a JWT/OAuth2 resource-server config would go.
 */
@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/documents/**").hasRole("EDITOR")
                .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/documents/**").hasRole("EDITOR")
                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/documents/**").hasRole("EDITOR")
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll())
            .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    UserDetailsService users(PasswordEncoder encoder) {
        return new InMemoryUserDetailsManager(
            User.withUsername("om")
                .password(encoder.encode("om-password"))
                .roles("EDITOR")
                .build(),
            User.withUsername("guest")
                .password(encoder.encode("guest-password"))
                .roles("VIEWER")
                .build());
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
