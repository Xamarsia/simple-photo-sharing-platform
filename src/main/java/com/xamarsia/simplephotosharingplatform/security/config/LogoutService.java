package com.xamarsia.simplephotosharingplatform.security.config;

import com.xamarsia.simplephotosharingplatform.security.AuthenticationConstants;
import com.xamarsia.simplephotosharingplatform.security.token.Token;
import com.xamarsia.simplephotosharingplatform.security.token.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final TokenRepository tokenRepository;

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        final String authHeader = request.getHeader(AuthenticationConstants.Validation.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith(AuthenticationConstants.Validation.BEARER)) {
            throw new RuntimeException("Logout failed. User is not authorized");
        }

        final String jwt = authHeader.substring(AuthenticationConstants.Validation.BEARER.length());

        Token token = tokenRepository.findByToken(jwt)
                .orElseThrow(() -> new RuntimeException("Logout failed. Token not found in repository"));
        tokenRepository.deleteById(token.id);
    }
}
