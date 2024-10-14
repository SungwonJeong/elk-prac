package com.example.elkprac.security.exception;

import com.example.elkprac.common.message.ErrorMessage;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        ErrorMessage errorMessage = throwErrorMessage(authException);
        SecurityErrorResponse.sendErrorResponse(response, errorMessage);
    }

    private ErrorMessage throwErrorMessage(AuthenticationException authException) {
        if (authException instanceof JwtValidationException) {
            return ((JwtValidationException) authException).getErrorMessage();
        }
        if (authException instanceof DisabledException) {
            return ErrorMessage.SECURITY_ACCOUNT_DISABLED;
        }
        if (authException instanceof LockedException) {
            return ErrorMessage.SECURITY_ACCOUNT_LOCKED;
        }
        if (authException instanceof InsufficientAuthenticationException) {
            return ErrorMessage.SECURITY_INSUFFICIENT_AUTHENTICATION;
        }
        if (authException instanceof InternalAuthenticationServiceException) {
            return ErrorMessage.SECURITY_AUTHENTICATION_SERVICE_ERROR;
        }
        if (authException instanceof UsernameNotFoundException) {
            return ErrorMessage.SECURITY_NOT_FOUND_USER;
        }

        log.error("다루지 못한 Security Error : {}", authException.getMessage(), authException);
        return ErrorMessage.SECURITY_UNAUTHORIZED_ACCESS;
    }
}
