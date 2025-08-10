package com.example.hanaro.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailySalesStats extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private LocalDate statsDate;

    private Long totalSalesAmount;

    private int totalOrderCount;

    // 상품별 통계와 1:N 관계
    @OneToMany(mappedBy = "dailySalesStats", cascade = CascadeType.ALL)
    private List<ProductSalesStats> productSalesStats = new ArrayList<>();

    @Builder
    public DailySalesStats(LocalDate statsDate, Long totalSalesAmount, int totalOrderCount) {
        this.statsDate = statsDate;
        this.totalSalesAmount = totalSalesAmount;
        this.totalOrderCount = totalOrderCount;
    }
}