package com.schoolmanagement.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration-ms}")
    private long jwtExpirationMs;

    @Value("${jwt.parent-expiration-ms}")
    private long parentExpirationMs;

    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String userId, String tenantId, String role, String name, String email) {
        return generateTokenWithExpiry(userId, tenantId, role, name, email, "USER", jwtExpirationMs);
    }

    public String generateParentToken(String parentId, String tenantId, String name, String email) {
        return generateTokenWithExpiry(parentId, tenantId, "PARENT", name, email, "PARENT", parentExpirationMs);
    }

    private String generateTokenWithExpiry(String userId, String tenantId, String role, String name, 
                                          String email, String type, long expiryMs) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiryMs);

        SecretKey key = getSigningKey();

        return Jwts.builder()
                .subject(userId)
                .claim("tenantId", tenantId)
                .claim("role", role)
                .claim("name", name)
                .claim("email", email)
                .claim("type", type)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        return validateAndGetClaims(token).isPresent();
    }

    public Optional<Claims> validateAndGetClaims(String token) {
        try {
            SecretKey key = getSigningKey();
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Optional.of(claims);
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty: {}", ex.getMessage());
        }
        return Optional.empty();
    }

    public Claims getClaims(String token) {
        try {
            SecretKey key = getSigningKey();
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException ex) {
            log.error("Failed to extract claims: {}", ex.getMessage());
            throw new IllegalArgumentException("Invalid token");
        }
    }

    public String getUserIdFromToken(String token) {
        return getClaims(token).getSubject();
    }

    public String getTenantIdFromToken(String token) {
        return (String) getClaims(token).get("tenantId");
    }

    public String getRoleFromToken(String token) {
        return (String) getClaims(token).get("role");
    }

    public String getTypeFromToken(String token) {
        return (String) getClaims(token).get("type");
    }

    public String getNameFromToken(String token) {
        return (String) getClaims(token).get("name");
    }

    public String getEmailFromToken(String token) {
        return (String) getClaims(token).get("email");
    }
}
