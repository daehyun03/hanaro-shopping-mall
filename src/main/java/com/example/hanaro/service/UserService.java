package com.example.hanaro.service;

import com.example.hanaro.dto.UserLoginRequestDto;
import com.example.hanaro.dto.UserSignupRequestDto;
import com.example.hanaro.entity.User;
import com.example.hanaro.enums.UserRole;
import com.example.hanaro.exception.CustomException;
import com.example.hanaro.exception.ErrorCode;
import com.example.hanaro.jwt.JwtTokenProvider;
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

    public String login(UserLoginRequestDto requestDto) {
        User user = userRepository.findByEmail(requestDto.email())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(requestDto.password(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        Map<String, Object> claims = new java.util.HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("role",user.getRole());
        String token = jwtTokenProvider.generateToken(claims, 10);
        return token;

    }
}