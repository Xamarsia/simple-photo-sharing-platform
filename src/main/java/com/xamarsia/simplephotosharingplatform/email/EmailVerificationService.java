package com.xamarsia.simplephotosharingplatform.email;

import com.xamarsia.simplephotosharingplatform.ApplicationConstants;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;


@Service
@AllArgsConstructor
public class EmailVerificationService {
    private final EmailVerificationRepository repository;
    private final MailSenderService mailService;

    public void sendEmailVerificationCode(@NotNull @NotEmpty String email) {
        EmailVerification verificationCode = createNewVerificationCode(email);
        mailService.sendEmailVerificationCode(email, verificationCode.verificationCode);
    }

    private Integer generateCode() {
        int length = ApplicationConstants.Validation.EMAIL_VERIFICATION_CODE_LENGTH;
        boolean useLetters = false;
        boolean useNumbers = true;
        String code = RandomStringUtils.random(length, useLetters, useNumbers);
        return Integer.parseInt(code);
    }

    public boolean isCodeExist(String email) {
        return repository.existsByEmail(email);
    }

    private EmailVerification getCodeByEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email verification code not found with email " + email));
    }

    private boolean isCodeExpired(Integer lifeTimeInSeconds, EmailVerification code) {
        return ChronoUnit.SECONDS.between(LocalDateTime.now(), code.creationDateTime) >= lifeTimeInSeconds;
    }

//    public void deleteCodeByEmail(@NotNull @NotEmpty String email) {
//        if (!isCodeExist(email)) {
//            throw new IllegalArgumentException("Delete email verification code by email: Email code not found with email " + email);
//        }
//        repository.deleteByEmail(email);
//    }

    public EmailVerification createNewVerificationCode(String email) {
        if (isCodeExist(email)) {
            EmailVerification code = getCodeByEmail(email);
            boolean isCodeExpired = isCodeExpired(ApplicationConstants.Validation.FAILED_EMAIL_VERIFICATION_DELAY_TIME, code);
            if (isCodeExpired) {
                code.setVerificationCode(generateCode());
                code.setCreationDateTime(LocalDateTime.now());
                code.setIsUsed(false);
            } else {
                throw new RuntimeException("Email verification code cannot be created for less than 30 seconds.");
            }
            return repository.save(code);
        }
        EmailVerification code =
                EmailVerification.builder()
                        .creationDateTime(LocalDateTime.now())
                        .verificationCode(generateCode())
                        .email(email)
                        .isUsed(false)
                        .build();

        return repository.save(code);
    }

    public boolean isVerificationCodeCorrect(String email, Integer code) {
        EmailVerification savedCode = getCodeByEmail(email);
        if (isCodeExpired(ApplicationConstants.Validation.EMAIL_VERIFICATION_RESEND_TIME, savedCode)) {
            throw new RuntimeException("Email verification code has expired. Please, create a new one.");
        } else if (savedCode.isUsed) {
            throw new RuntimeException("Email verification code cannot be used twice. Please, wait " +
                    (ApplicationConstants.Validation.EMAIL_VERIFICATION_RESEND_TIME / 60) +
                    " minutes and create new one.");
        } else if (!Objects.equals(savedCode.getVerificationCode(), code)) {
            savedCode.setIsUsed(true);
            repository.save(savedCode);
            return false;
        }
        savedCode.setIsUsed(true);
        repository.save(savedCode);
        return true;
    }
}