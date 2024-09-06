package com.xamarsia.simplephotosharingplatform.exception;

import lombok.Getter;


@Getter
public enum ApplicationError {
    INVALID_REQUEST(1000),
    RESOURCE_NOT_FOUND(1001),
    VALIDATION_FAILED(1002),
    UNAUTHORIZED_ACCESS(1003),
    ACCESS_DENIED(1004),
    INTERNAL_SERVER_ERROR(1005),
    AWS_S3_ERROR(1009),
    TOO_MANY_REQUESTS(1010),
    CONSTRAINT_VIOLATION_FAILED(1012),
    UNIQUE_USERNAME_CONSTRAINT_FAILED(1014),
    ILLEGAL_ARGUMENT_EXCEPTION(1015),
    UNIQUE_AUTHENTICATION_CONSTRAINT_FAILED(1016),
    ;

    private final int value;

    private ApplicationError(int value) {
        this.value = value;
    }

    public String getError() {
        return name()+ "_ERROR";
    }
}