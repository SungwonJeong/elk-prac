package com.example.elkprac.common.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ResponseMessage {

    SIGNUP_SUCCESS("회원가입에 성공하였습니다", HttpStatus.CREATED),
    LOGIN_SUCCESS("로그인에 성공하였습니다", HttpStatus.OK),
    LOGOUT_SUCCESS("로그아웃에 성공하였습니다", HttpStatus.OK),
    TOKEN_ISSUANCE_SUCCESS("토큰 재발행에 성공하였습니다", HttpStatus.OK),
    ACCESS_TOKEN_VALID("Access Token이 아직 유효합니다.", HttpStatus.OK),

    PRODUCT_REGISTER_SUCCESS("상품 등록 성공", HttpStatus.CREATED),
    PRODUCT_FETCH_SUCCESS("상품 조회 성공", HttpStatus.OK);

    private final String message;
    private final HttpStatus status;
}

