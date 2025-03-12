package microservice.authenticationservice.com.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import microservice.authenticationservice.com.entity.AppUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public final class JwtGenerator {

    @Value("${spring.security.jwt.secret}")
    private String SECRET_KEY;

    @Value("${spring.security.jwt.expiration}")
    private long EXPIRATION_KEY;

    private Key getSignKey() {
        byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Map<String, Object> createClaims(AppUser appUser) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", appUser.getUsername());
        claims.put("email", appUser.getEmail());
        claims.put("role", appUser.getAppUserRole().name());
        return claims;
    }

    public Date getExpirationDate() {
        return new Date(System.currentTimeMillis() + EXPIRATION_KEY);
    }

    public String generateToken(AppUser appUser) {
        return Jwts.builder()
                .setClaims(createClaims(appUser))
                .setSubject(String.valueOf(appUser.getId()))
                .setIssuedAt(new Date())
                .setExpiration(getExpirationDate())
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}
