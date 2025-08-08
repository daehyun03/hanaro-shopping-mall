package com.example.hanaro.config.swagger.response;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponse(responseCode = "401", description = "인증 실패",
        content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"errorCode\": \"E401001\", \"message\": \"인증에 실패했습니다.\"}")
        )
)
public @interface Api401Error {
}
