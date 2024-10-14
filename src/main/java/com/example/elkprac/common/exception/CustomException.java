package com.example.elkprac.common.exception;

import com.example.elkprac.common.message.ErrorMessage;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{

    private final ErrorMessage errorMessage;
    private final String traceId;  // traceId 필드 추가

    public CustomException(ErrorMessage errorMessage) {
        super(errorMessage.getMessage());
        this.errorMessage = errorMessage;
        this.traceId = null;
    }

    public CustomException(ErrorMessage errorMessage, String traceId) {
        super(errorMessage.getMessage());
        this.errorMessage = errorMessage;
        this.traceId = traceId;
    }
}
