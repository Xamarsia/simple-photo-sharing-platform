package com.xamarsia.simplephotosharingplatform.security.authentication;

import com.xamarsia.simplephotosharingplatform.exception.ApplicationError;
import com.xamarsia.simplephotosharingplatform.exception.exceptions.ApplicationException;
import com.xamarsia.simplephotosharingplatform.exception.exceptions.ResourceNotFoundException;
import com.xamarsia.simplephotosharingplatform.exception.exceptions.UnauthorizedAccessException;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;

@Service
@Validated
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

    public Auth saveAuthentication(Authentication authentication) {
        if (authentication instanceof AnonymousAuthenticationToken) {
            throw new UnauthorizedAccessException("[SaveAuthentication]: User not authenticated.");
        }

        String id = authentication.getName();
        if (repository.existsById(id)) {
            Auth auth = getAuthenticationById(id);
            if (auth.user != null) {
                throw new ApplicationException(ApplicationError.UNIQUE_AUTHENTICATION_CONSTRAINT_FAILED,
                        String.format("[SaveAuthentication]: Authentication with id '%s' already exist.", id));
            }
            return auth;
        }

        Auth auth = Auth.builder()
                .id(id)
                .build();

        return saveAuthentication(auth);
    }

    public Auth saveAuthentication(Auth authentication) {
        try {
            return repository.save(authentication);
        } catch (Exception e) {
            throw new ApplicationException(ApplicationError.INTERNAL_SERVER_ERROR,
                    "[SaveAuthentication]: " + e.getMessage());
        }
    }

    private Auth getAuthenticationById(String id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(
                String.format("[GetAuthenticationById]: Authentication not found with id '%s'.", id)));
    }
}
