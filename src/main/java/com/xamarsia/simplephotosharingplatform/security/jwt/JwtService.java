package com.xamarsia.simplephotosharingplatform.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@Service
public class JwtService {
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    public String getSubject(String token) {
        return extractAllClaims(token).getSubject();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(String userId) {
        return generateToken(new HashMap<>(), userId);
    }

    public String generateToken(
            Map<String, Object> extraClaims,
            String userId
    ) {
        return buildToken(extraClaims, userId, jwtExpiration);
    }

    public String generateRefreshToken(String userId) {
        return buildToken(new HashMap<>(), userId, refreshExpiration);
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            String userId,
            long expiration) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userId)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, String userId) {
        final String userIdFromToken = getSubject(token);
        return (userIdFromToken.equals(userId) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        Date today = Date.from(Instant.now());
        return extractAllClaims(token).getExpiration().before(today);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
