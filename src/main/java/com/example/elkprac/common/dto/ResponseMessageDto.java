package com.example.elkprac.common.dto;

import com.example.elkprac.common.message.ResponseMessage;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseMessageDto<T> {

    private final String message;
    private final HttpStatus status;
    private final T data;

    public static<T> ResponseEntity<ResponseMessageDto<T>> toResponseEntity(ResponseMessage responseMessage, T data) {
        return ResponseEntity
                .status(responseMessage.getStatus())
                .body(ResponseMessageDto.<T>builder()
                        .message(responseMessage.getMessage())
                        .status(responseMessage.getStatus())
                        .data(data)
                        .build());
    }
}

