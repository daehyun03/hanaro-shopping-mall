package com.example.hanaro.service;

import com.example.hanaro.dto.TokenResponseDto;
import com.example.hanaro.dto.UserLoginRequestDto;
import com.example.hanaro.dto.UserResponseDto;
import com.example.hanaro.dto.UserSignupRequestDto;
import com.example.hanaro.entity.RefreshToken;
import com.example.hanaro.entity.User;
import com.example.hanaro.enums.UserRole;
import com.example.hanaro.exception.CustomException;
import com.example.hanaro.enums.ErrorCode;
import com.example.hanaro.jwt.JwtTokenProvider;
import com.example.hanaro.repository.RefreshTokenRepository;
import com.example.hanaro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

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
        log.info("새로운 사용자 회원가입 완료: {}", user.getId());
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
        log.info("사용자 로그인 성공: {}", user.getEmail());
        return new TokenResponseDto(accessToken, refreshTokenValue);
    }
    @Transactional(readOnly = true)
    public List<UserResponseDto> getUserList() {
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(UserResponseDto::new)
                .collect(Collectors.toList());
    }

    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        if (user.getRole() == UserRole.ROLE_ADMIN) {
            throw new CustomException(ErrorCode.CANNOT_DELETE_ADMIN);
        }
        userRepository.delete(user);
        log.info("사용자 삭제 완료: {}", user.getId());
    }
}