package com.example.hanaro.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true)
    private String tokenValue;

    @Builder
    public RefreshToken(User user, String tokenValue) {
        this.user = user;
        this.tokenValue = tokenValue;
    }

    // 토큰 값을 갱신하는 메서드
    public void updateTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }
}