package com.snack.security;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class ShopJwtUtil {
    private final SecretKey key;
    private final long expiration;

    public ShopJwtUtil(@Value("${jwt.secret:snack-manager-secret-key-2026-default}") String secret,
                       @Value("${jwt.expiration:604800000}") long expiration) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = expiration;
    }

    public String generateToken(Long userId, String username) {
        return Jwts.builder()
            .subject(String.valueOf(userId))
            .claim("username", username)
            .claim("userType", "SHOP")
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(key)
            .compact();
    }

    public Long getUserId(String token) {
        return Long.parseLong(Jwts.parser().verifyWith(key).build()
            .parseSignedClaims(token).getPayload().getSubject());
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) { return false; }
    }
}
