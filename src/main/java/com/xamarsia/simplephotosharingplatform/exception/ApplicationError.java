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
    JWT_ERROR(1006),
    BAD_CREDENTIALS(1007),
    EMAIL_VERIFICATION_CODE_FAIL(1008),
    AWS_S3_ERROR(1009),
    TOO_MANY_REQUESTS(1010),
    INVALID_EMAIL_VERIFICATION(1011),
    CONSTRAINT_VIOLATION_FAILED(1012)
    ;

    private final int value;

    private ApplicationError(int value) {
        this.value = value;
    }

    public String getError() {
        return name()+ "_ERROR";
    }
}