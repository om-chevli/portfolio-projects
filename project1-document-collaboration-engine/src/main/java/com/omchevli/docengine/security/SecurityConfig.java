package com.omchevli.docengine.security;

import java.util.List;

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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

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
            .cors(Customizer.withDefaults())
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

    /**
     * CORS for the local demo only: the React dev server on localhost:5173 is
     * the one browser origin allowed to call /api/**, with the Authorization
     * header the frontend sends (HTTP Basic) and credentials allowed so the
     * browser will send it. Any other origin gets no CORS headers.
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
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
