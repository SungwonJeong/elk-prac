package com.example.elkprac.common.exception;

import com.example.elkprac.common.dto.ErrorMessageDto;
import com.example.elkprac.common.message.ErrorMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorMessageDto> handleCustomException(CustomException e) {
        return handleException(e, e.getErrorMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessageDto> handleNotValidFormatException(MethodArgumentNotValidException e) {
        List<String> errorMessages = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage()).toList();

        String combinedErrorMessage = String.join(", ", errorMessages);

        ErrorMessageDto errorMessageDto = ErrorMessageDto.builder()
                .message(combinedErrorMessage)
                .status(HttpStatus.BAD_REQUEST.value())
                .build();
        return ResponseEntity.status(errorMessageDto.getStatus()).body(errorMessageDto);
    }

//    @ExceptionHandler(RedisConnectionFailureException.class)
//    public ResponseEntity<ErrorMessageDto> handleRedisConnectionFailure(RedisConnectionFailureException e) {
//        return handleException(e, ErrorMessage.REDIS_SERVICE_UNAVAILABLE);
//    }


    private ResponseEntity<ErrorMessageDto> handleException(Exception e, ErrorMessage errorMessage) {
        logException(e);
        return ErrorMessageDto.toResponseEntity(errorMessage);
    }

    private void logException(Exception e) {
        log.error("Exception: {} - {}", e.getClass().getSimpleName(), e.getMessage(), e);
        if (e instanceof CustomException customException) {
            log.error("[ERROR] {}", customException.getErrorMessage());
        }
    }
}
