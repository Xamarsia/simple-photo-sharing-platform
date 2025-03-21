package com.xamarsia.simplephotosharingplatform.security.authentication;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {

    private final AuthService service;
    AntPathMatcher pathMatcher = new AntPathMatcher();

    @Value("#{'${application.security.authentication-filter.excludedUrl}'.split(',')}")
    private List<String> excludedUrlPatterns;

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) throws ServletException {
        return excludedUrlPatterns.stream()
                .anyMatch(p -> pathMatcher.match(p, request.getServletPath()));
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        if (!service.authExistsById(auth.getName())) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
