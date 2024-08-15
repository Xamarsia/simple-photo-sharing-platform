package com.xamarsia.simplephotosharingplatform.security.jwt;

import com.xamarsia.simplephotosharingplatform.security.token.Token;
import com.xamarsia.simplephotosharingplatform.security.token.TokenRepository;
import com.xamarsia.simplephotosharingplatform.security.token.TokenType;
import com.xamarsia.simplephotosharingplatform.user.User;

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

@Service
public class JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    private final TokenRepository tokenRepository;

    public JwtService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public String getSubject(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String generateToken(String userId) {
        return generateToken(new HashMap<>(), userId);
    }

    public String generateToken(Map<String, Object> extraClaims, String userId) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userId)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, String userId) {
        final String userIdFromToken = getSubject(token);
        return (userIdFromToken.equals(userId) && !isTokenExpired(token) && isTokenExist(token));
    }

    private boolean isTokenExist(String token) {
        Token storedToken = tokenRepository.findByToken(token).orElse(null);

        return (storedToken != null);
    }

    private boolean isTokenExpired(String token) {
        Date today = Date.from(Instant.now());
        return extractAllClaims(token).getExpiration().before(today);
    }

    private Claims extractAllClaims(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
        return claims;
    }

    public void deleteExpiredToken(String token, String userId) {
        if (!isTokenValid(token, userId)) {
            Token storedToken = tokenRepository.findByToken(token).orElse(null);

            if (storedToken != null) {
                tokenRepository.deleteById(storedToken.id);
            }
        }
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public void deleteAllUserTokens(Long userId) {
        var validUserTokens = tokenRepository.findAllByUser_Id(userId);
        if (validUserTokens.isEmpty())
            return;

        validUserTokens.forEach(storedToken -> {
            tokenRepository.deleteById(storedToken.id);
        });
    }

    public void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .build();
        tokenRepository.save(token);
    }
}
