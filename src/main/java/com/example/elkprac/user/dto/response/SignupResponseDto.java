package com.example.elkprac.user.dto.response;

import com.example.elkprac.user.entity.User;
import lombok.Getter;

@Getter
public class SignupResponseDto {

    private final Long userId;
    private final String username;

    private SignupResponseDto(User user) {
        this.userId = user.getId();
        this.username = user.getUsername();
    }

    public static SignupResponseDto from(User user) {
        return new SignupResponseDto(user);
    }
}
