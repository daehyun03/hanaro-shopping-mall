package com.example.hanaro.service;

import com.example.hanaro.dto.TokenResponseDto;
import com.example.hanaro.dto.UserLoginRequestDto;
import com.example.hanaro.dto.UserSignupRequestDto;
import com.example.hanaro.entity.RefreshToken;
import com.example.hanaro.entity.User;
import com.example.hanaro.enums.UserRole;
import com.example.hanaro.exception.CustomException;
import com.example.hanaro.exception.ErrorCode;
import com.example.hanaro.jwt.JwtTokenProvider;
import com.example.hanaro.repository.RefreshTokenRepository;
import com.example.hanaro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void signup(UserSignupRequestDto requestDto) {
        if (userRepository.findByEmail(requestDto.email()).isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }
        String encodedPassword = passwordEncoder.encode(requestDto.password());

        User user = User.builder()
                .email(requestDto.email())
                .password(encodedPassword)
                .nickname(requestDto.nickname())
                .role(UserRole.ROLE_USER) // 기본 역할은 USER
                .build();
        userRepository.save(user);
    }

    public TokenResponseDto login(UserLoginRequestDto requestDto) {
        User user = userRepository.findByEmail(requestDto.email())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(requestDto.password(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user.getEmail(), user.getRole());
        String refreshTokenValue = jwtTokenProvider.generateRefreshToken(user.getEmail());
        // Refresh Token을 DB에 저장 (이미 있으면 갱신, 없으면 새로 생성)
        refreshTokenRepository.findByUserId(user.getId())
                .ifPresentOrElse(
                        // 이미 토큰이 있는 경우: 토큰 값만 갱신
                        refreshToken -> refreshToken.updateTokenValue(refreshTokenValue),
                        // 토큰이 없는 경우: 새로 만들어서 저장
                        () -> refreshTokenRepository.save(new RefreshToken(user, refreshTokenValue))
                );
        return new TokenResponseDto(accessToken, refreshTokenValue);
    }
}