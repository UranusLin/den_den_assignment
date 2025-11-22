package com.denden.assignment.service.impl;

import com.denden.assignment.model.User;
import com.denden.assignment.repository.UserRepository;
import com.denden.assignment.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public LocalDateTime getLastLoginTime(String email) {
        return userRepository.findByEmail(email)
                .map(User::getLastLoginTime)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
