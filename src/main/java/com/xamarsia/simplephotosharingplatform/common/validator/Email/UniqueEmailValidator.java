package com.xamarsia.simplephotosharingplatform.common.validator.Email;

import com.xamarsia.simplephotosharingplatform.user.UserService;
import lombok.NonNull;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@AllArgsConstructor
@Component
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {
    private final UserService service;

    @Override
    public boolean isValid(@NonNull final String email, ConstraintValidatorContext context) {
        return !service.isEmailUsed(email);
    }
}
