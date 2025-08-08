package com.example.hanaro.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 모든 엔티티의 공통 속성 (생성일, 수정일)을 관리하는 추상 클래스
 */
@MappedSuperclass // 테이블로 매핑하지 않고, 자식 클래스에 필드를 상속시켜주는 어노테이션
@EntityListeners(AuditingEntityListener.class) // JPA Auditing 기능 활성화
public abstract class BaseEntity {

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt; // 생성 시간

    @LastModifiedDate
    private LocalDateTime updatedAt; // 수정 시간
}