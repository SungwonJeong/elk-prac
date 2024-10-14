package com.example.elkprac.user.dto.response;

import com.example.elkprac.user.entity.User;
import lombok.Getter;

@Getter
public class LoginResponseDto {

    private final Long userId;
    private final String username;
    private final String accessToken;
    private final String refreshToken;

    private LoginResponseDto(User user, String accessToken, String refreshToken) {
        this.userId = user.getId();
        this.username = user.getUsername();
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public static LoginResponseDto of(User user, String accessToken, String refreshToken) {
        return new LoginResponseDto(user, accessToken, refreshToken);
    }
}
