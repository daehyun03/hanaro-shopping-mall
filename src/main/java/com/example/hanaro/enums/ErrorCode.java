package com.example.hanaro.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 4xx CLIENT_ERROR
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "E400001", "유효하지 않은 파라미터입니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "E401001", "비밀번호가 일치하지 않습니다."),
    UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "E401002", "인증이 필요한 서비스입니다."),
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "E403001", "접근 권한이 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "E404001", "가입되지 않은 이메일입니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "E404002", "존재하지 않는 상품입니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "E409001", "이미 사용 중인 이메일입니다."),

    // 5xx SERVER_ERROR
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E500001", "서버에 오류가 발생했습니다."),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E500002", "데이터베이스에 오류가 발생했습니다."),
    FILE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E500003", "파일 업로드 중 오류가 발생했습니다."),
    BATCH_JOB_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E500004", "배치 작업 처리 중 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}