package com.xamarsia.simplephotosharingplatform.exception.applicationException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.xamarsia.simplephotosharingplatform.enums.ApplicationError;


@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
public class AWSException extends ApplicationException {
    public AWSException(String message) {
        super(ApplicationError.AWS_S3_ERROR, message);
    }
}
