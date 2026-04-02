package com.skax.scm.gateway.api.dto;

/**
 * 토큰 상태 enum
 * LoginAuthFilter에서 사용
 */
public enum TokenStatus {
    TOKEN_OK("0", "정상"),
    TOKEN_NOTOK("1", "토큰 오류"),
    TOKEN_EXPIRED("2", "토큰 만료"),
    ACCESS_TOKEN_EXPIRED("2", "액세스 토큰 만료"),
    REFRESH_TOKEN_EXPIRED("3", "리프레시 토큰 만료"),
    TOKEN_INVALID("4", "유효하지 않은 토큰"),
    TOKEN_MISSING("5", "토큰 없음"),
    TOKEN_MALFORMED("6", "토큰 형식 오류"),
    IP_NOT_ALLOWED("7", "허용되지 않은 IP"),
    USER_NOT_FOUND("8", "사용자 없음"),
    AUTH_FAILED("9", "인증 실패"),
    SYSTEM_ERROR("10", "시스템 오류");

    private final String code;
    private final String message;

    TokenStatus(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return code + ":" + message;
    }
}