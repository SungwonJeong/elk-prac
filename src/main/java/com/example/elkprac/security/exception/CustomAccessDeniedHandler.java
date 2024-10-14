package com.example.elkprac.security.exception;

import com.example.elkprac.common.message.ErrorMessage;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private static final String DEFAULT_ROLE = "ROLE_USER";

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        String role = getUserRole();
        ErrorMessage errorMessage = selectErrorMessage(role);
        SecurityErrorResponse.sendErrorResponse(response, errorMessage);
    }

    // 사용자 역할 추출 로직을 별도 메서드로 분리하여 역할 명확화
    private String getUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getAuthorities().isEmpty()) {
            return DEFAULT_ROLE;
        }
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse(DEFAULT_ROLE);
    }

    // 역할에 따른 오류 메시지 결정
    private ErrorMessage selectErrorMessage(String role) {
        return switch (role) {
            case "ROLE_ADMIN" -> ErrorMessage.ACCESS_DENIED_ADMIN;
            case "ROLE_SELLER" -> ErrorMessage.ACCESS_DENIED_SELLER;
            default -> ErrorMessage.ACCESS_DENIED_USER;
        };
    }
}
