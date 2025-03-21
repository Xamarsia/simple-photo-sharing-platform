package com.xamarsia.simplephotosharingplatform.common.validator.Username;

import org.springframework.stereotype.Component;

import com.xamarsia.simplephotosharingplatform.user.UserService;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
@Component
public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, String> {
    private final UserService service;

    @Override
    public boolean isValid(@NonNull final String username, ConstraintValidatorContext context) {
        return !service.isUsernameUsed(username);
    }
}
