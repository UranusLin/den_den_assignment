package com.denden.assignment.service;

import com.denden.assignment.dto.AuthDto;
import com.denden.assignment.model.User;
import com.denden.assignment.repository.UserRepository;
import com.denden.assignment.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private AuthDto.RegisterRequest registerRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = new AuthDto.RegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password");

        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPasswordHash("encodedPassword");
        user.setEnabled(true);
    }

    @Test
    void register_ShouldSaveUser_WhenEmailIsNew() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        authService.register(registerRequest);

        verify(userRepository).save(any(User.class));
        verify(emailService).sendActivationEmail(anyString(), anyString());
    }

    @Test
    void register_ShouldThrowException_WhenEmailExists() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        assertThrows(RuntimeException.class, () -> authService.register(registerRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_ShouldSend2FA_WhenCredentialsValid() {
        AuthDto.LoginRequest loginRequest = new AuthDto.LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);

        authService.login(loginRequest);

        verify(userRepository).save(user); // Saves 2FA code
        verify(emailService).sendTwoFactorCode(eq("test@example.com"), anyString());
    }

    @Test
    void verify2FA_ShouldReturnToken_WhenCodeValid() {
        user.setTwoFactorCode("123456");
        user.setTwoFactorExpiry(LocalDateTime.now().plusMinutes(5));

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        AuthDto.Verify2FARequest verifyRequest = new AuthDto.Verify2FARequest();
        verifyRequest.setEmail("test@example.com");
        verifyRequest.setCode("123456");

        String token = authService.verify2FA(verifyRequest);

        assertNotNull(token);
        assertNull(user.getTwoFactorCode()); // Should be cleared
    }
}
