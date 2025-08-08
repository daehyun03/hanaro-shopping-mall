package com.example.hanaro.config.swagger.response;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음",
        content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"errorCode\": \"E404001\", \"message\": \"리소스를 찾을 수 없습니다.\"}")
        )
)
public @interface Api404Error {
}
