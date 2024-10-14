package com.example.elkprac.security.exception;

import com.example.elkprac.common.dto.ErrorMessageDto;
import com.example.elkprac.common.message.ErrorMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.OutputStream;

public class SecurityErrorResponse {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void sendErrorResponse(HttpServletResponse response, ErrorMessage errorMessage) throws IOException {
        // ErrorMessageDto를 생성
        ErrorMessageDto errorMessageDto = ErrorMessageDto.builder()
                .code(errorMessage.getCode())
                .message(errorMessage.getMessage())
                .status(errorMessage.getStatus().value())
                .build();

        // 응답의 콘텐츠 타입과 캐릭터 인코딩 설정
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(errorMessage.getStatus().value());

        // try-with-resources로 OutputStream을 자동으로 닫음
        try (OutputStream outputStream = response.getOutputStream()) {
            objectMapper.writeValue(outputStream, errorMessageDto);
        }
    }
}
