package com.example.elkprac.user.dto.request;

import com.example.elkprac.user.entity.User;
import com.example.elkprac.user.entity.UserRole;

public record SignupRequestDto(
        String username,
        String password
) {

    public User toEntity(String username, String password, UserRole role) {
        return User.builder()
                .username(username)
                .password(password)
                .role(role)
                .build();
    }
}
