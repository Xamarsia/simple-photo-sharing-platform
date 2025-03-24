package com.xamarsia.simplephotosharingplatform.security.authentication;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import com.xamarsia.simplephotosharingplatform.exception.ApplicationError;
import com.xamarsia.simplephotosharingplatform.exception.exceptions.ApplicationException;
import com.xamarsia.simplephotosharingplatform.exception.exceptions.ResourceNotFoundException;
import com.xamarsia.simplephotosharingplatform.exception.exceptions.UnauthorizedAccessException;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthService {
    private final AuthRepository repository;

    public Boolean authExistsById(@NotBlank @PathVariable String id) {
        return repository.existsById(id);
    }

    public Auth getAuthentication(Authentication authentication) {
        if (authentication instanceof AnonymousAuthenticationToken) {
            throw new UnauthorizedAccessException("[GetAuthentication]: User not authenticated.");
        }
        String id = authentication.getName();
        return getAuthenticationById(id);
    }

    public Auth createAuth(Authentication authentication) {
        if (authentication instanceof AnonymousAuthenticationToken) {
            throw new UnauthorizedAccessException("[CreateAuth]: User not authenticated.");
        }

        String id = authentication.getName();
        if (repository.existsById(id)) {
            Auth auth = getAuthenticationById(id);
            if (auth.user != null) {
                throw new ApplicationException(ApplicationError.UNIQUE_AUTH_CONSTRAINT_FAILED,
                        String.format("[CreateAuth]: Authentication with id '%s' already exist.", id));
            }
            return auth;
        }

        Auth auth = Auth.builder()
                .id(id)
                .build();

        return saveAuth(auth);
    }

    public Auth saveAuth(Auth auth) {
        try {
            return repository.save(auth);
        } catch (Exception e) {
            throw new ApplicationException(ApplicationError.INTERNAL_SERVER_ERROR,
                    "[SaveAuth]: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public Boolean isAuthUsed(Authentication authentication) {
        Auth auth = getAuthentication(authentication);
        return auth.getUser() != null;
    }

    private Auth getAuthenticationById(String id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(
                String.format("[GetAuthenticationById]: Authentication not found with id '%s'.", id)));
    }
}
