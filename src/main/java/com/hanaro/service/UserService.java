package com.hanaro.service;

import com.hanaro.dto.UserLoginRequestDto;
import com.hanaro.dto.UserSignupRequestDto;
import com.hanaro.entity.User;
import com.hanaro.enums.UserRole;
import com.hanaro.jwt.JwtTokenProvider;
import com.hanaro.repository.UserRepository;
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
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
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
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        if (!passwordEncoder.matches(requestDto.password(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        Map<String, Object> claims = new java.util.HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("role",user.getRole());
        String token = jwtTokenProvider.generateToken(claims, 10);
        return token;

    }
}