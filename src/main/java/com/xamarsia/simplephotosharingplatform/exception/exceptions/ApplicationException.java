package com.xamarsia.simplephotosharingplatform.exception.exceptions;

import com.xamarsia.simplephotosharingplatform.exception.ApplicationError;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class ApplicationException extends RuntimeException {
    private ApplicationError errorCode;
    private String message;
}
