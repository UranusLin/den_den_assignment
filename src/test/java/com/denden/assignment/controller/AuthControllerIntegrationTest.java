package com.denden.assignment.controller;

import com.denden.assignment.dto.AuthDto;
import com.denden.assignment.model.User;
import com.denden.assignment.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void register_ShouldReturn200_WhenRequestIsValid() throws Exception {
        AuthDto.RegisterRequest request = new AuthDto.RegisterRequest();
        request.setEmail("integration@example.com");
        request.setPassword("password");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void login_ShouldReturn200_WhenUserExistsAndActive() throws Exception {
        // Setup user
        User user = new User();
        user.setEmail("login@example.com");
        // Manually hash password for test or inject encoder. 
        // Since we can't easily inject encoder into test setup without autowiring, 
        // let's rely on the fact that the service uses the bean.
        // Actually, for integration test, we should save user with hashed password.
        // We can use a simple BCrypt hash for "password" -> $2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG
        user.setPasswordHash("$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG"); 
        user.setEnabled(true);
        userRepository.save(user);

        AuthDto.LoginRequest request = new AuthDto.LoginRequest();
        request.setEmail("login@example.com");
        request.setPassword("password");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
