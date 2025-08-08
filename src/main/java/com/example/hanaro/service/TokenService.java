package com.example.hanaro.service;

import com.example.hanaro.entity.RefreshToken;
import com.example.hanaro.entity.User;
import com.example.hanaro.jwt.JwtTokenProvider;
import com.example.hanaro.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public String reissueAccessToken(String refreshTokenValue) {
        // Refresh Token 유효성 검증 (DB에 있는지 확인)
        RefreshToken refreshToken = refreshTokenRepository.findByTokenValue(refreshTokenValue)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired refresh token."));

        // Refresh Token 자체의 유효기간도 검증
        jwtTokenProvider.validateToken(refreshTokenValue);

        // 토큰에서 User 정보 추출
        User user = refreshToken.getUser();

        // 새로운 Access Token 생성
        return jwtTokenProvider.generateAccessToken(user.getEmail(), user.getRole());
    }
}