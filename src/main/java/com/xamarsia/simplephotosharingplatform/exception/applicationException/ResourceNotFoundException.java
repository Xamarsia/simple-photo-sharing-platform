package com.xamarsia.simplephotosharingplatform.exception.applicationException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.xamarsia.simplephotosharingplatform.enums.ApplicationError;


@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends ApplicationException {
    public ResourceNotFoundException(String message) {
        super(ApplicationError.RESOURCE_NOT_FOUND, message);
    }
}
