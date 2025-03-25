package com.xamarsia.simplephotosharingplatform.enums;

import lombok.Getter;

@Getter
public enum ApplicationError {
    RESOURCE_NOT_FOUND(1000),
    VALIDATION_FAILED(1001),
    UNAUTHORIZED_ACCESS(1002),
    ACCESS_DENIED(1003),
    INTERNAL_SERVER_ERROR(1004),
    AWS_S3_ERROR(1005),
    TOO_MANY_REQUESTS(1006),
    CONSTRAINT_VIOLATION_FAILED(1007),
    UNIQUE_USERNAME_CONSTRAINT_FAILED(1008),
    ILLEGAL_ARGUMENT_EXCEPTION(1009),
    UNIQUE_AUTH_CONSTRAINT_FAILED(1010),
    ;

    private final int value;

    private ApplicationError(int value) {
        this.value = value;
    }

    public String getError() {
        return name() + "_ERROR";
    }
}
