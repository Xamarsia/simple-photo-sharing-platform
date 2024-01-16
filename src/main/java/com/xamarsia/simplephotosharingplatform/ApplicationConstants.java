package com.xamarsia.simplephotosharingplatform;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ApplicationConstants {
    @UtilityClass
    public class Validation {
        public static final Integer EMAIL_VERIFICATION_CODE_LENGTH = 8;
        public static final Integer EMAIL_VERIFICATION_RESEND_TIME = 60;
        public static final Integer FAILED_EMAIL_VERIFICATION_DELAY_TIME = 30;
        public static final Integer EMAIL_VERIFICATION_CODE_LIFETIME_IN_SECONDS = 12*60*60;
    }
}