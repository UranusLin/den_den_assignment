package com.denden.assignment.config;

import com.denden.assignment.model.User;
import com.denden.assignment.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final UserRepository userRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        User user = userRepository.findBySessionToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid session token"));

        if (!user.isEnabled()) {
            throw new RuntimeException("User account is disabled");
        }

        // Store user ID in request for controller to use
        request.setAttribute("userId", user.getId());
        request.setAttribute("userEmail", user.getEmail());
        
        return true;
    }
}
