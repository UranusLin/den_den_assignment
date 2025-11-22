package com.denden.assignment.controller;

import com.denden.assignment.dto.ApiResponse;
import com.denden.assignment.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "會員管理相關 API")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @GetMapping("/me/last-login")
    @Operation(summary = "查詢最後登入時間 (需帶入 Authorization Header)")
    public ResponseEntity<ApiResponse<LocalDateTime>> getLastLoginTime(@RequestAttribute("userEmail") String email) {
        LocalDateTime lastLogin = userService.getLastLoginTime(email);
        return ResponseEntity.ok(ApiResponse.success(lastLogin));
    }
}
