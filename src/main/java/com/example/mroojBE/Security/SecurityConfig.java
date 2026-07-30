package com.example.mroojBE.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * TEMPORARY / PHASE-0 security config.
 *
 * Purpose: unblock manual Postman testing of Booking/Assignment endpoints
 * while the real JWT filter chain (JwtAuthFilter, UserDetailsService,
 * login/register flow) is still being built.
 *
 * What this does:
 *   - Disables the default Spring Security auto-config (generated user +
 *     /login form) that was intercepting every request.
 *   - permitAll() on every path — NO real authorization yet.
 *   - Disables CSRF — irrelevant for a stateless JSON API, and would
 *     otherwise block every non-GET Postman request with 403.
 *   - STATELESS session policy — no server-side session/cookie, since a
 *     JWT-based API shouldn't rely on HttpSession once auth is added.
 *
 * MUST be replaced before production: swap permitAll() for explicit
 * per-endpoint rules and register the JWT filter once it exists. Left as
 * a loud, unmissable TODO rather than silently shipping this as-is.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // TODO(PHASE-JWT): remove permitAll(), wire JwtAuthFilter here,
    // restrict by role (FARMER / CONSULTANT / ADMIN) per endpoint.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                .httpBasic(basic -> basic.disable())
                .formLogin(form -> form.disable());

        return http.build();
    }

    /**
     * Registered now (not in Phase-JWT) because AuthService will need it
     * for password hashing regardless of when JWT lands — RegisterRequestDTO
     * already documents that the caller must pass an encoded password.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}