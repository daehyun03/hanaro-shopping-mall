package com.example.hanaro.controller;

import com.example.hanaro.config.swagger.response.Api401Error;
import com.example.hanaro.config.swagger.response.Api500ErrorGroup;
import com.example.hanaro.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "인증/인가 관련 API")
@RestController
@RequiredArgsConstructor
public class TokenController {

    private final TokenService tokenService;

    @Operation(summary = "Access Token 재발급",
            description = "만료된 Access Token을 Refresh Token을 이용해 재발급합니다.")
    @ApiResponse(responseCode = "200", description = "Access Token 재발급 성공")
    @Api401Error
    @Api500ErrorGroup
    @PostMapping("/api/token/reissue") // ⬅️ /api 접두어 추가 (일관성)
    public ResponseEntity<String> reissueAccessToken(
            @RequestHeader("Authorization-Refresh") String refreshTokenValue) {

        String newAccessToken = tokenService.reissueAccessToken(refreshTokenValue);
        return ResponseEntity.ok(newAccessToken);
    }
}