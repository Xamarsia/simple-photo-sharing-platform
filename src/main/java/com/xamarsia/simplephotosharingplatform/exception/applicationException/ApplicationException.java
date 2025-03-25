package com.xamarsia.simplephotosharingplatform.exception.applicationException;

import com.xamarsia.simplephotosharingplatform.enums.ApplicationError;

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
