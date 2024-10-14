package com.example.elkprac.user.controller;

import com.example.elkprac.common.dto.ResponseMessageDto;
import com.example.elkprac.common.message.ResponseMessage;
import com.example.elkprac.user.dto.request.LoginRequestDto;
import com.example.elkprac.user.dto.request.SignupRequestDto;
import com.example.elkprac.user.dto.response.LoginResponseDto;
import com.example.elkprac.user.dto.response.SignupResponseDto;
import com.example.elkprac.user.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ResponseMessageDto<SignupResponseDto>> signup(@RequestBody SignupRequestDto signupRequestDto) {
        SignupResponseDto response = authService.signup(signupRequestDto);
        return ResponseMessageDto.toResponseEntity(ResponseMessage.SIGNUP_SUCCESS, response);
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseMessageDto<LoginResponseDto>> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse httpServletResponse) {
        LoginResponseDto response = authService.login(loginRequestDto, httpServletResponse);
        return ResponseMessageDto.toResponseEntity(ResponseMessage.LOGIN_SUCCESS, response);
    }
}
