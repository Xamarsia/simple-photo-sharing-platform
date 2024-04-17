package com.xamarsia.simplephotosharingplatform.security.config;

import com.xamarsia.simplephotosharingplatform.exception.exceptions.InvalidJWTException;
import com.xamarsia.simplephotosharingplatform.exception.exceptions.UnauthorizedAccessException;
import com.xamarsia.simplephotosharingplatform.security.token.Token;
import com.xamarsia.simplephotosharingplatform.security.token.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {
    private final TokenRepository tokenRepository;

    @Value("${application.security.token-type}")
    private String tokenType;

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith(tokenType)) {
            throw new UnauthorizedAccessException("[logout]: Logout failed. User is not authorized.");
        }

        final String jwt = authHeader.substring(tokenType.length());

        Token token = tokenRepository.findByToken(jwt)
                .orElseThrow(() -> new InvalidJWTException("[logout]: Logout failed. Token not found in repository."));
        tokenRepository.deleteById(token.id);
    }
}
