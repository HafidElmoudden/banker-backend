package com.hafidelmoudden.bankerbackend.services;

import com.hafidelmoudden.bankerbackend.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtService {

    private final SecretKey secretKey;
    
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
    
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    public JwtService(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    public String generateToken(User user) {
        return generateToken(createClaims(user), user.getUsername(), jwtExpiration);
    }

    public String generateRefreshToken(User user) {
        return generateToken(new HashMap<>(), user.getUsername(), refreshExpiration);
    }

    private Map<String, Object> createClaims(User user) {
        Map<String, Object> claims = new HashMap<>();
        String authorities = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        claims.put("roles", authorities);
        return claims;
    }

    private String generateToken(Map<String, Object> claims, String subject, long expirationMinutes) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationMinutes * 60 * 1000))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenValid(String token, User user) {
        final String username = extractUsername(token);
        return (username.equals(user.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
} 