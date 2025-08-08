package com.example.hanaro.config.swagger.response;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponse(responseCode = "400", description = "잘못된 입력값 또는 요청 형식",
        content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"errorCode\": \"E401001\", \"message\": \"유효하지 않은 파라미터입니다.\"}")
        )
)
public @interface Api400Error {
}
