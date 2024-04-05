package com.xamarsia.simplephotosharingplatform.exception.exceptions;

import com.xamarsia.simplephotosharingplatform.exception.ApplicationError;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
public class UnauthorizedAccessException extends ApplicationException {
    public UnauthorizedAccessException(String message) {
        super(ApplicationError.UNAUTHORIZED_ACCESS, message);
    }
}
