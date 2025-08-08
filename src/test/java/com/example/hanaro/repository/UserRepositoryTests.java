package com.example.hanaro.repository;

import com.example.hanaro.entity.User;
import com.example.hanaro.enums.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class UserRepositoryTests {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @Transactional
    @Commit
    @DisplayName("초기 사용자 데이터 생성 (관리자 1명, 일반 사용자 10명)")
    void createInitialUsers() {
        // 데이터가 이미 존재하면 중복 생성을 방지하기 위해 실행하지 않음
        if (userRepository.count() > 0) {
            System.out.println("데이터가 이미 존재하여 생성을 건너뜁니다.");
            return;
        }
        // 1. 관리자 계정 생성
        User admin = User.builder()
                .email("admin@hanaro.com")
                .password(passwordEncoder.encode("admin1234"))
                .nickname("관리자")
                .role(UserRole.ROLE_ADMIN)
                .build();
        userRepository.save(admin);

        // 2. 더미 일반 사용자 10명 생성
        for (int i = 1; i <= 10; i++) {
            User user = User.builder()
                    .email("user" + i + "@hanaro.com")
                    .password(passwordEncoder.encode("user1234"))
                    .nickname("사용자" + i)
                    .role(UserRole.ROLE_USER)
                    .build();
            userRepository.save(user);
        }

        // 3. 최종적으로 11명의 사용자가 생성되었는지 확인
        assertThat(userRepository.count()).isEqualTo(11);
        System.out.println("초기 사용자 데이터 생성 완료. 총 " + userRepository.count() + "명");
    }
}


