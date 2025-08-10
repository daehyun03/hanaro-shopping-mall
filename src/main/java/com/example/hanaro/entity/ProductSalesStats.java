package com.example.hanaro.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductSalesStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;
    private String productName;
    private int totalQuantity;
    private Long totalAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_sales_stats_id")
    private DailySalesStats dailySalesStats;

    @Builder
    public ProductSalesStats(Long productId, String productName, int totalQuantity, Long totalAmount, DailySalesStats dailySalesStats) {
        this.productId = productId;
        this.productName = productName;
        this.totalQuantity = totalQuantity;
        this.totalAmount = totalAmount;
        this.dailySalesStats = dailySalesStats;
    }

}
