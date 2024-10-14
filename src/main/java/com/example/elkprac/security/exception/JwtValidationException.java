package com.example.elkprac.security.exception;

import com.example.elkprac.common.message.ErrorMessage;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class JwtValidationException extends AuthenticationException {

    private final ErrorMessage errorMessage;

    public JwtValidationException(ErrorMessage errorMessage) {
        super(errorMessage.getMessage());
        this.errorMessage = errorMessage;
    }
}
