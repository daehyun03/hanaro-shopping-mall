package com.example.hanaro.config.swagger.response;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponse(responseCode = "409", description = "중복된 값",
        content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"errorCode\": \"E409001\", \"message\": \"중복된 값이 존재합니다.\"}")
        )
)
public @interface Api409Error {
}
