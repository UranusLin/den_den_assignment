package com.denden.assignment.controller;

import com.denden.assignment.dto.ApiResponse;
import com.denden.assignment.dto.AuthDto;
import com.denden.assignment.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "會員註冊與登入相關 API")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "註冊新會員")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody AuthDto.RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok(ApiResponse.success("Registration successful. Please check your email to activate account.", null));
    }

    @GetMapping("/activate")
    @Operation(summary = "啟用帳號 (透過 Token)")
    public ResponseEntity<ApiResponse<String>> activate(@RequestParam String token) {
        authService.activateAccount(token);
        return ResponseEntity.ok(ApiResponse.success("Account activated successfully. You can now login.", null));
    }

    @PostMapping("/login")
    @Operation(summary = "登入 (第一步): 驗證帳密並發送 2FA 驗證碼")
    public ResponseEntity<ApiResponse<String>> login(@Valid @RequestBody AuthDto.LoginRequest request) {
        authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Credentials valid. 2FA code sent to your email.", null));
    }

    @PostMapping("/login/verify")
    @Operation(summary = "登入 (第二步): 驗證 2FA 驗證碼")
    public ResponseEntity<ApiResponse<String>> verify2FA(@Valid @RequestBody AuthDto.Verify2FARequest request) {
        String token = authService.verify2FA(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", token));
    }
}
