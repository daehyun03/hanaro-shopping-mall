package com.example.hanaro.controller;

import com.example.hanaro.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TokenController {

    private final TokenService tokenService;

    @PostMapping("/token/reissue")
    public ResponseEntity<String> reissueAccessToken(@RequestHeader("Authorization-Refresh") String refreshTokenValue) {
        String newAccessToken = tokenService.reissueAccessToken(refreshTokenValue);
        return ResponseEntity.ok(newAccessToken);
    }
}