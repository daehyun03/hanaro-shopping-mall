package com.example.hanaro.config.swagger.response;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponse(responseCode = "403", description = "권한 없음",
        content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"errorCode\": \"E403001\", \"message\": \"접근 권한이 없습니다.\"}")
        )
)
public @interface Api403Error {
}
