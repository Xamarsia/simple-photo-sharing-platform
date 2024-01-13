package com.xamarsia.simplephotosharingplatform.email;

import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class MailSenderService {

    private final JavaMailSender mailVerificationSender;

    private void sendNewVerificationMail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailVerificationSender.send(message);
    }

    public void sendEmailVerificationCode(String to, Integer code) {
        String subject = "[SPSP] Confirm Your Email";
        String body = "To confirm your identity on SPSP, we need to verify your email address. " +
                "This code can only be used once. If you didn't request a code, please ignore " +
                "this email. Never share this code with anyone else. Your email confirmation code: " +
                code.toString();
        sendNewVerificationMail(to, subject, body);
    }
}
