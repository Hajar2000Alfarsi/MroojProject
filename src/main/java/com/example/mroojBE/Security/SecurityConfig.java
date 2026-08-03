package com.example.mroojBE.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

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


    // restrict by role (FARMER / CONSULTANT / ADMIN) per endpoint.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))   // ← ADD THIS LINE
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
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}