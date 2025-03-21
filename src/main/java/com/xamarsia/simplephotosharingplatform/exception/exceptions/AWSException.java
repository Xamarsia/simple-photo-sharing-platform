package com.xamarsia.simplephotosharingplatform.exception.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.xamarsia.simplephotosharingplatform.exception.ApplicationError;


@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
public class AWSException extends ApplicationException {
    public AWSException(String message) {
        super(ApplicationError.AWS_S3_ERROR, message);
    }
}
