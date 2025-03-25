package com.xamarsia.simplephotosharingplatform.exception.applicationException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.xamarsia.simplephotosharingplatform.enums.ApplicationError;


@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
public class UnauthorizedAccessException extends ApplicationException {
    public UnauthorizedAccessException(String message) {
        super(ApplicationError.UNAUTHORIZED_ACCESS, message);
    }
}
