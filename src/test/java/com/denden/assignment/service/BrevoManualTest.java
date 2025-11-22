package com.denden.assignment.service;

import com.denden.assignment.service.impl.BrevoEmailService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "BREVO_API_KEY", matches = ".*")
@TestPropertySource(properties = "app.email.provider=brevo")
class BrevoManualTest {

    @Autowired
    private EmailService emailService;

    @Value("${brevo.sender-email:test@example.com}")
    private String senderEmail;

    @Test
    void sendTestEmail() throws InterruptedException {
        // Sending to the sender email itself for verification
        System.out.println("Attempting to send email to: " + senderEmail);
        emailService.sendActivationEmail(senderEmail, "http://localhost:8080/activate?token=test-token");
        System.out.println("Email request submitted. Waiting 5 seconds for async process to complete...");
        Thread.sleep(5000); // Wait for async execution
        System.out.println("Check logs for success/failure message.");
    }
}
