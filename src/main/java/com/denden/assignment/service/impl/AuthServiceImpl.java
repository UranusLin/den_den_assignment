package com.denden.assignment.service.impl;

import com.denden.assignment.dto.AuthDto;
import com.denden.assignment.exception.AppException;
import com.denden.assignment.model.User;
import com.denden.assignment.repository.UserRepository;
import com.denden.assignment.service.AuthService;
import com.denden.assignment.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.base-url}")
    private String baseUrl;

    @Override
    @Transactional
    public void register(AuthDto.RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new AppException("Email already registered");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setActivationToken(UUID.randomUUID().toString());
        user.setEnabled(false);

        userRepository.save(user);

        String activationLink = baseUrl + "/api/auth/activate?token=" + user.getActivationToken();
        emailService.sendActivationEmail(user.getEmail(), activationLink);
    }

    @Override
    @Transactional
    public void activateAccount(String token) {
        User user = userRepository.findByActivationToken(token)
                .orElseThrow(() -> new AppException("Invalid activation token"));
        
        user.setEnabled(true);
        user.setActivationToken(null);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void login(AuthDto.LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException("User not found", org.springframework.http.HttpStatus.UNAUTHORIZED));

        if (!user.isEnabled()) {
            throw new AppException("Account not activated", org.springframework.http.HttpStatus.FORBIDDEN);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new AppException("Invalid credentials", org.springframework.http.HttpStatus.UNAUTHORIZED);
        }

        // Generate 6-digit code
        String code = String.format("%06d", new SecureRandom().nextInt(1000000));
        user.setTwoFactorCode(code);
        user.setTwoFactorExpiry(LocalDateTime.now().plusMinutes(5));
        userRepository.save(user);

        emailService.sendTwoFactorCode(user.getEmail(), code);
    }

    @Override
    @Transactional
    public String verify2FA(AuthDto.Verify2FARequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getTwoFactorCode() == null || !user.getTwoFactorCode().equals(request.getCode())) {
            throw new AppException("Invalid 2FA code", org.springframework.http.HttpStatus.UNAUTHORIZED);
        }

        if (user.getTwoFactorExpiry().isBefore(LocalDateTime.now())) {
            throw new AppException("2FA code expired", org.springframework.http.HttpStatus.UNAUTHORIZED);
        }

        // Clear 2FA code
        user.setTwoFactorCode(null);
        user.setTwoFactorExpiry(null);
        
        // Generate session token
        String sessionToken = UUID.randomUUID().toString();
        user.setSessionToken(sessionToken);
        
        // Update last login
        user.setLastLoginTime(LocalDateTime.now());
        userRepository.save(user);

        return sessionToken;
    }
}
