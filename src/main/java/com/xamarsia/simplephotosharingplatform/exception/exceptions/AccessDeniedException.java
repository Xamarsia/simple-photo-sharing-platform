package com.xamarsia.simplephotosharingplatform.exception.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.xamarsia.simplephotosharingplatform.exception.ApplicationError;


@ResponseStatus(code = HttpStatus.FORBIDDEN)
public class AccessDeniedException extends ApplicationException {
    public AccessDeniedException(String message) {
        super(ApplicationError.ACCESS_DENIED, message);
    }
}
