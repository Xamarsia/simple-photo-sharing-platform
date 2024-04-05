package com.xamarsia.simplephotosharingplatform.exception.exceptions;


import com.xamarsia.simplephotosharingplatform.exception.ApplicationError;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(code = HttpStatus.TOO_MANY_REQUESTS)
public class TooManyRequestsException extends ApplicationException {
    public TooManyRequestsException(String message) {
        super(ApplicationError.TOO_MANY_REQUESTS, message);
    }
}
