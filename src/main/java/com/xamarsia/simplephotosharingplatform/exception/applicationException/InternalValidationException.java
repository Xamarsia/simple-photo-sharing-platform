package com.xamarsia.simplephotosharingplatform.exception.applicationException;

import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.xamarsia.simplephotosharingplatform.enums.ApplicationError;

import jakarta.validation.ConstraintViolation;


@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalValidationException extends ApplicationException {
    public InternalValidationException(String message) {
        super(ApplicationError.CONSTRAINT_VIOLATION_FAILED, message);
    }

    public <T> InternalValidationException(Set<ConstraintViolation<T>> violations) {
        super(ApplicationError.CONSTRAINT_VIOLATION_FAILED, "");
        List<String> violationsMessages = violations.stream()
                .map(violation -> String.format("%s Current value is '%s'.", violation.getMessage(),
                violation.getInvalidValue())).toList();

        StringBuilder errors = new StringBuilder("Internal validation failed: ");
        for (String error : violationsMessages) {
            errors.append(error);
        }
        this.setMessage(errors.toString());
    }
}
