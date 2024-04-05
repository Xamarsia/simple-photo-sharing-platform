package com.xamarsia.simplephotosharingplatform.exception.exceptions;

import com.xamarsia.simplephotosharingplatform.exception.ApplicationError;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends ApplicationException {
    public ResourceNotFoundException(String message) {
        super(ApplicationError.RESOURCE_NOT_FOUND, message);
    }
}
