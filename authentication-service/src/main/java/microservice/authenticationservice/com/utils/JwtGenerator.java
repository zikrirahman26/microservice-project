package microservice.authenticationservice.com.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import microservice.authenticationservice.com.entity.AppUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;

@Service
public final class JwtGenerator {

    @Value("${spring.security.jwt.secret}")
    private String SECRET_KEY;

    @Value("${spring.security.jwt.expiration}")
    private long EXPIRATION_KEY;

    private Key getSignKey() {
        byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("Secret key must be at least 256 bits (32 bytes) for HS256.");
        }

        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Map<String, Object> createClaims(AppUser appUser) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", appUser.getUsername());
        claims.put("email", appUser.getEmail());
        claims.put("role", appUser.getAppUserRole().name());
        return claims;
    }

    public String generateToken(AppUser appUser) {
        return Jwts.builder()
                .setHeaderParam("alg", "HS256")
                .setHeaderParam("typ", "JWT")
                .setClaims(createClaims(appUser))
                .setSubject(String.valueOf(appUser.getId()))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_KEY))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}
