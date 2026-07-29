package com.example.mroojBE.Security;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

//checks the token on every single request
//filter is a piece of code that every incoming request must pass through before
//reaching your endpoints.

@Component
public class JwtAuthFilter {
    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        // 1. Look for the Authorization header
        String authHeader = request.getHeader("Authorization");

        // 2. If it's missing or doesn't start with "Bearer ", skip this filter —
        //    maybe this is a public endpoint like /api/auth/login
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Cut off "Bearer " (7 characters) to get the raw token string
        String token = authHeader.substring(7);

        // 4. If the token is valid, tell Spring Security who this user is
        if (jwtUtil.isTokenValid(token)) {
            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractRole(token);

            SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(email, null, List.of(authority));

            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        // 5. Always continue to the next filter, whether or not authentication was set
        filterChain.doFilter(request, response);
    }

}
