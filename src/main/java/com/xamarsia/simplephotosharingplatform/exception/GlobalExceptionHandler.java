package com.xamarsia.simplephotosharingplatform.exception;

import com.xamarsia.simplephotosharingplatform.exception.exceptions.ApplicationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;


@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> onConstraintValidationException(ConstraintViolationException exception, HttpServletRequest request) {
        StringBuilder errors = new StringBuilder("Constraint violation failed: ");
        for (ConstraintViolation violation : exception.getConstraintViolations()) {
            String error = String.format("%s ", violation.getMessage());
            errors.append(error);
        }

        ErrorResponse error = new ErrorResponse(ApplicationError.CONSTRAINT_VIOLATION_FAILED.getValue(), errors.toString());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> onMethodArgumentNotValidException(MethodArgumentNotValidException exception, HttpServletRequest request) {
        StringBuilder errors = new StringBuilder("Validation failed: ");
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            String error = String.format("[%s]: %s ", fieldError.getField(), fieldError.getDefaultMessage());
            errors.append(error);
        }

        ErrorResponse error = new ErrorResponse(ApplicationError.VALIDATION_FAILED.getValue(), errors.toString());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> onBadCredentialsException(BadCredentialsException exception, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(ApplicationError.BAD_CREDENTIALS.getValue(), exception.getMessage());

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorResponse> onApplicationException(ApplicationException exception, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(exception.getErrorCode().getValue(), exception.getMessage());
        ResponseStatus responseStatus = exception.getClass().getAnnotation(ResponseStatus.class);

        return new ResponseEntity<>(response, responseStatus.code());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> onException(Exception exception, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(ApplicationError.INTERNAL_SERVER_ERROR.getValue(), exception.getMessage());

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
