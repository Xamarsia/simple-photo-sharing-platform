package com.xamarsia.simplephotosharingplatform.security.config;

import com.xamarsia.simplephotosharingplatform.security.ApplicationConstants;
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


        final String authHeader = request.getHeader(ApplicationConstants.Validation.AUTHORIZATION);

        if (authHeader == null ||!authHeader.startsWith(ApplicationConstants.Validation.BEARER)) {
            return;
        }

        final String jwt = authHeader.substring(ApplicationConstants.Validation.BEARER.length());
        tokenRepository.findByToken(jwt)
                .ifPresent(storedToken -> tokenRepository.deleteById(storedToken.id));
    }
}
