package spring.app.Mobile.service.impl;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import spring.app.Mobile.service.interfaces.JwtService;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JwtServiceImpl implements JwtService {

    private final String jwtSecret;
    private final Long expiration;

    public JwtServiceImpl(@Value("${jwt.secret}") String jwtSecret, @Value("${jwt.expiration}") Long expiration) {
        this.jwtSecret = jwtSecret;
        this.expiration = expiration;
    }

    @Override
    public String generateToken(Map<String, Object> claims) {
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setNotBefore(now)
                .setExpiration(new Date(now.getTime() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public Claims extractClaims(String token) {
        try {
            JwtParser jwtParser = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build();
            return jwtParser.parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            throw new RuntimeException("Invalid token", e);
        }
    }

    @Override
    public String generatePasswordResetToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("email_verification", "purpose");
        return generateToken(claims);
    }

    @Override
    public String generateEmailVerificationToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("password_reset", "purpose");
        return generateToken(claims);
    }

    @Override
    public boolean validateEmailVerificationToken(String token) {
        Claims claims = extractClaims(token);
        return "email_verification".equals(claims.get("purpose"));
    }

    @Override
    public boolean validatePasswordResetToken(String token) {
        Claims claims = extractClaims(token);
        return "password_reset".equals(claims.get("purpose"));
    }

    @Override
    public String generateBearerToken(String uuid, Map<String, List<String>> roles) {
        Date now = new Date();

        return Jwts.builder()
                .setClaims(roles)
                .setSubject(uuid)
                .setIssuedAt(now)
                .setNotBefore(now)
                .setExpiration(new Date(now.getTime() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
