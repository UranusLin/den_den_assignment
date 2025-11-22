package com.denden.assignment.service;

import com.denden.assignment.dto.AuthDto;

public interface AuthService {
    void register(AuthDto.RegisterRequest request);
    void activateAccount(String token);
    void login(AuthDto.LoginRequest request);
    String verify2FA(AuthDto.Verify2FARequest request);
}
