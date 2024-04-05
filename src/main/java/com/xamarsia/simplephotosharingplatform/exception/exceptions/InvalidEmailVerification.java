package com.xamarsia.simplephotosharingplatform.exception.exceptions;

import com.xamarsia.simplephotosharingplatform.exception.ApplicationError;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class InvalidEmailVerification extends ApplicationException {
    public InvalidEmailVerification(String message) {
        super(ApplicationError.INVALID_EMAIL_VERIFICATION, message);
    }
}