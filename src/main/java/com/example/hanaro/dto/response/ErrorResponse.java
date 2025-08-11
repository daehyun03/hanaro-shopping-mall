package com.example.hanaro.dto.response;

/**
 * API 에러 발생 시, 클라이언트에게 반환할 공통 응답 DTO
 * @param errorCode 커스텀 에러 코드
 * @param message 에러 상세 메시지
 */
public record ErrorResponse(String errorCode, String message) {
}