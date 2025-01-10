package com.example.demo.utils;

import com.example.demo.auth.entity.User;
import com.example.demo.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticatedUserUtils {

    private final UserService userService;

    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginId = authentication.getName();

        if (loginId == null || loginId.isEmpty()) {
            throw new IllegalArgumentException("인증된 사용자가 없습니다.");
        }

        return userService.findUserByLoginId(loginId);
    }

}
