package com.denden.assignment.service;

import java.time.LocalDateTime;

public interface UserService {
    LocalDateTime getLastLoginTime(String email);
}
