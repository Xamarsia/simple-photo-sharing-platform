package com.xamarsia.simplephotosharingplatform.security.jwt;

import com.xamarsia.simplephotosharingplatform.security.ApplicationConstants;
import com.xamarsia.simplephotosharingplatform.security.token.Token;
import com.xamarsia.simplephotosharingplatform.security.token.TokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
public class JwtService {
    private final TokenRepository tokenRepository;

    public JwtService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public String getSubject(String token) {
        return extractAllClaims(token).getSubject();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(String email) {
        return generateToken(new HashMap<>(), email);
    }

    public String generateToken(Map<String, Object> extraClaims, String email) {
        return Jwts.builder().setClaims(extraClaims).setSubject(email).setIssuedAt(Date.from(Instant.now())).setExpiration(Date.from(Instant.now().plus(15, DAYS))).signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
    }

    public boolean isTokenValid(String token, String email) {
        final String emailFromToken = getSubject(token);
        return (emailFromToken.equals(email) && !isTokenExpired(token) && isTokenExist(token));
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

    public void deleteExpiredToken(String token, String email) {
        if (!isTokenValid(token, email)) {
            Token storedToken = tokenRepository.findByToken(token).orElse(null);

            if (storedToken != null) {
                tokenRepository.deleteById(storedToken.id);
            }
        }
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(ApplicationConstants.Validation.SECRET_KEY.getBytes());
    }
}
