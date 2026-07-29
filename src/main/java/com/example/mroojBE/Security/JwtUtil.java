package com.example.mroojBE.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {
    //pulls the secret value from our application.properties
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    // turns our plain-text secret into a proper cryptographic key
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // 1. CREATE a token for a given email and role
    public String generateToken(String email, String role) {
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    // 2. READ the email out of a token
    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    // 3. READ the role out of a token
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    // 4. CHECK whether a token is still valid (correctly signed and not expired)
    public boolean isTokenValid(String token) {
        try {
            Date expiry = extractAllClaims(token).getExpiration();
            return expiry.after(new Date()); // not expired yet
        } catch (Exception e) {
            return false; // bad signature, malformed token, etc. → treat as invalid
        }
    }

    // helper: opens the token, verifies its signature, and reads all claims (data) inside it
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
