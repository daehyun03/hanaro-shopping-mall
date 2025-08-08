package com.example.hanaro.config.swagger.response;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses(@ApiResponse(responseCode = "500", description = "서버 내부 오류",
        content = @Content(mediaType = "application/json",
                examples = {
                        @ExampleObject(name = "일반 서버 에러", value = "{\"errorCode\": \"E500001\", \"message\": \"서버에 오류가 발생했습니다.\"}"),
                        @ExampleObject(name = "데이터베이스 에러", value = "{\"errorCode\": \"E500002\", \"message\": \"데이터베이스에 오류가 발생했습니다.\"}"),
                        @ExampleObject(name = "파일 업로드 에러", value = "{\"errorCode\": \"E500003\", \"message\": \"파일 업로드 중 오류가 발생했습니다.\"}"),
                        @ExampleObject(name = "배치 작업 에러", value = "{\"errorCode\": \"E500004\", \"message\": \"배치 작업 처리 중 오류가 발생했습니다.\"}")
                }
        )
))
public @interface Api500ErrorGroup {
}
