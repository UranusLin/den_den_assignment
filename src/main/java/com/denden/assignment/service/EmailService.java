package com.denden.assignment.service;

public interface EmailService {
    void sendActivationEmail(String to, String activationLink);
    void sendTwoFactorCode(String to, String code);
}
