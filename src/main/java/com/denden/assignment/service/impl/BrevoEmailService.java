package com.denden.assignment.service.impl;

import brevo.ApiClient;
import brevo.ApiException;
import brevo.Configuration;
import brevo.auth.ApiKeyAuth;
import brevoApi.TransactionalEmailsApi;
import brevoModel.SendSmtpEmail;
import brevoModel.SendSmtpEmailSender;
import brevoModel.SendSmtpEmailTo;
import com.denden.assignment.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@ConditionalOnProperty(name = "app.email.provider", havingValue = "brevo")
public class BrevoEmailService implements EmailService {

    @Value("${brevo.api-key}")
    private String apiKey;

    @Value("${brevo.sender-email}")
    private String senderEmail;

    @Override
    @Async
    public void sendActivationEmail(String to, String activationLink) {
        sendEmail(to, "Activate your account", "Click here to activate: " + activationLink);
    }

    @Override
    @Async
    public void sendTwoFactorCode(String to, String code) {
        sendEmail(to, "Your 2FA Code", "Your login code is: " + code);
    }

    private void sendEmail(String to, String subject, String content) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        ApiKeyAuth apiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("api-key");
        

        
        apiKeyAuth.setApiKey(apiKey);

        TransactionalEmailsApi api = new TransactionalEmailsApi();
        SendSmtpEmail sendSmtpEmail = new SendSmtpEmail();
        
        sendSmtpEmail.setSender(new SendSmtpEmailSender().email(senderEmail).name("DenDen Assignment"));
        sendSmtpEmail.setTo(List.of(new SendSmtpEmailTo().email(to)));
        sendSmtpEmail.setSubject(subject);
        sendSmtpEmail.setHtmlContent("<h3>" + content + "</h3>");
        sendSmtpEmail.setTextContent(content);

        try {
            api.sendTransacEmail(sendSmtpEmail);
            log.info("Brevo email sent to {}", to);
        } catch (ApiException e) {
            log.error("Error sending Brevo email to {}: {}", to, e.getResponseBody(), e);
        }
    }
}
