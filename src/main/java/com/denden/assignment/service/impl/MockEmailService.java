package com.denden.assignment.service.impl;

import com.denden.assignment.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@ConditionalOnProperty(name = "app.email.provider", havingValue = "mock", matchIfMissing = true)
public class MockEmailService implements EmailService {

    @Override
    @Async
    public void sendActivationEmail(String to, String activationLink) {
        log.info("Mocking activation email to {}: Link: {}", to, activationLink);
    }

    @Override
    @Async
    public void sendTwoFactorCode(String to, String code) {
        log.info("Mocking 2FA email to {}: Code: {}", to, code);
    }
}
