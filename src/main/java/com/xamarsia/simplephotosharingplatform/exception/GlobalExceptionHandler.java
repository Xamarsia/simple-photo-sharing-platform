package com.xamarsia.simplephotosharingplatform.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.xamarsia.simplephotosharingplatform.enums.ApplicationError;
import com.xamarsia.simplephotosharingplatform.exception.applicationException.ApplicationException;
import com.xamarsia.simplephotosharingplatform.response.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> onConstraintValidationException(ConstraintViolationException exception,
            HttpServletRequest request) {
        StringBuilder errors = new StringBuilder("Constraint violation failed: ");
        for (ConstraintViolation<?> violation : exception.getConstraintViolations()) {
            String error = String.format("%s ", violation.getMessage());
            errors.append(error);
        }

        ErrorResponse error = new ErrorResponse(ApplicationError.CONSTRAINT_VIOLATION_FAILED.getValue(),
                errors.toString());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> onMethodArgumentNotValidException(MethodArgumentNotValidException exception,
            HttpServletRequest request) {
        StringBuilder errors = new StringBuilder("Validation failed: ");
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            String error = String.format("[%s]: %s ", fieldError.getField(), fieldError.getDefaultMessage());
            errors.append(error);
        }

        ErrorResponse error = new ErrorResponse(ApplicationError.VALIDATION_FAILED.getValue(), errors.toString());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> onIllegalArgumentException(IllegalArgumentException exception,
            HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(ApplicationError.ILLEGAL_ARGUMENT_EXCEPTION.getValue(),
                exception.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorResponse> onApplicationException(ApplicationException exception,
            HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(exception.getErrorCode().getValue(), exception.getMessage());

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> onException(Exception exception, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(ApplicationError.INTERNAL_SERVER_ERROR.getValue(),
                exception.getMessage());

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
