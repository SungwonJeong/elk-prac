package com.example.elkprac.common.dto;

import com.example.elkprac.common.message.ErrorMessage;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@Getter
@Builder
public class ErrorMessageDto {

    private final String code;
    private final String timestamp = LocalDateTime.now().toString();
    private final String message;
    private final int status;

    public static ResponseEntity<ErrorMessageDto> toResponseEntity(ErrorMessage errorMessage) {
        return ResponseEntity
                .status(errorMessage.getStatus())
                .body(ErrorMessageDto.builder()
                        .code(errorMessage.getCode())
                        .message(errorMessage.getMessage())
                        .status(errorMessage.getStatus().value())
                        .build());
    }
}
