package com.example.mroojBE.Security;

// This file decides which
//endpoints are public, which need a login, and which require a specific role. This is where RBAC
//truly comes alive.
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF is for browser-session/cookie based apps — not needed for a stateless JWT API
                .csrf(csrf -> csrf.disable())

                // decide who can access what, based on the role inside the JWT
                .authorizeHttpRequests(auth -> auth
                        // public: login and registration must be reachable without a token
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/farmers/register").permitAll()
                        .requestMatchers("/api/consultants/register").permitAll()

                        // farmer-only actions
                        .requestMatchers("/api/bookings/create").hasRole("FARMER")

                        // consultant-only actions
                        .requestMatchers("/api/bookings/*/respond").hasRole("CONSULTANT")
                        .requestMatchers("/api/bookings/*/response").hasRole("CONSULTANT")
                        .requestMatchers("/api/consultants/*/availability").hasRole("CONSULTANT")

                        // shared: both farmers and consultants can view/manage their own bookings & appointments
                        .requestMatchers("/api/bookings/**").hasAnyRole("FARMER", "CONSULTANT")
                        .requestMatchers("/api/appointments/**").hasAnyRole("FARMER", "CONSULTANT")

                        // everything else still requires a valid login, but no specific role
                        .anyRequest().authenticated()
                )

                // no sessions — every request must prove itself with a token
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // plug our JWT checkpoint in before Spring's default login filter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
