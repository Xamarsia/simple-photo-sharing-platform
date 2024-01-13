package com.xamarsia.simplephotosharingplatform.email;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailBeans {
    @Bean
    public JavaMailSender getVerificationJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(System.getenv("VERIFICATION_MAIL_HOST"));
        mailSender.setPort(Integer.parseInt(System.getenv("MAIL_PORT")));

        mailSender.setUsername(System.getenv("VERIFICATION_MAIL_USERNAME"));
        mailSender.setPassword(System.getenv("VERIFICATION_MAIL_PASSWORD"));

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "false"); // true

        return mailSender;
    }
}
