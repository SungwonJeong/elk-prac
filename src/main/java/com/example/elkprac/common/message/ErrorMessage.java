package com.example.elkprac.common.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorMessage {

    // Auth
    NOT_FOUND_USER("AUTH001","존재하지 않은 사용자입니다", HttpStatus.NOT_FOUND),
    DUPLICATED_USERNAME("AUTH002","중복된 이름이 존재합니다.", HttpStatus.BAD_REQUEST),
    NOT_VALID_PASSWORD("AUTH003","비밀번호가 일치하지 않습니다", HttpStatus.BAD_REQUEST),
    LOGIN_REQUIRED("AUTH004","로그인을 해주세요", HttpStatus.UNAUTHORIZED),
    FORBIDDEN_ACCESS("AUTH005","해당 작업에 대한 권한이 없습니다", HttpStatus.FORBIDDEN),

    // Security
    SECURITY_ACCOUNT_DISABLED("SEC001", "탈퇴한 계정입니다.", HttpStatus.UNAUTHORIZED),
    SECURITY_ACCOUNT_LOCKED("SEC002", "계정이 잠겼습니다.", HttpStatus.UNAUTHORIZED),
    SECURITY_INSUFFICIENT_AUTHENTICATION("SEC003", "인증이 부족합니다.", HttpStatus.FORBIDDEN),
    SECURITY_AUTHENTICATION_SERVICE_ERROR("SEC004", "인증 서비스 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    SECURITY_UNAUTHORIZED_ACCESS("SEC005", "허가되지 않은 접근입니다.", HttpStatus.UNAUTHORIZED),
    SECURITY_NOT_FOUND_USER("SEC006", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    SECURITY_UNAUTHORIZED("SEC007", "인증되지 않은 사용자입니다.", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED_ADMIN("SEC008", "관리자만 접근할 수 있습니다.", HttpStatus.FORBIDDEN),
    ACCESS_DENIED_SELLER("SEC009", "판매자 이상만 접근할 수 있습니다.", HttpStatus.FORBIDDEN),
    ACCESS_DENIED_USER("SEC010", "사용자는 이 페이지에 접근할 수 없습니다.", HttpStatus.FORBIDDEN),

    // JWT
    TOKEN_ERROR("JWT001","토큰 검증 중 에러가 발생하였습니다.", HttpStatus.UNAUTHORIZED),

    EXPIRED_ACCESS_TOKEN("JWT002", "엑세스 토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
    INVALID_ACCESS_FORMAT("JWT003", "토큰 형식이 잘못되었습니다.", HttpStatus.BAD_REQUEST),
    INVALID_ACCESS_SIGNATURE("JWT004", "토큰 서명이 유효하지 않습니다.", HttpStatus.UNAUTHORIZED),
    UNSUPPORTED_ACCESS_TOKEN("JWT005", "지원되지 않는 토큰입니다.", HttpStatus.BAD_REQUEST),

    INVALID_REFRESH_FORMAT("JWT006", "리프레시 토큰 형식이 잘못되었습니다.", HttpStatus.BAD_REQUEST),
    INVALID_REFRESH_SIGNATURE("JWT007", "리프레시 토큰 서명이 유효하지 않습니다.", HttpStatus.UNAUTHORIZED),
    EXPIRED_REFRESH_TOKEN("JWT008", "리프레시 토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
    UNSUPPORTED_REFRESH_TOKEN("JWT009", "지원되지 않는 리프레시 토큰입니다.", HttpStatus.BAD_REQUEST),

    // Product
    NOT_FOUND_PRODUCT("PD001", "상품이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    INVALID_CONNECTION_TYPE_NAME("PD002", "올바른 연결방식 이름이 아닙니다.", HttpStatus.BAD_REQUEST),

    // DB
    DATABASE_ERROR("DB001","데이터베이스 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    REDIS_SERVICE_UNAVAILABLE("DB002","Redis 서버 연결 실패했습니다.", HttpStatus.SERVICE_UNAVAILABLE),

    // Server
    SYSTEM_ERROR("SB001","시스템 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
