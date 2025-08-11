package com.example.hanaro.dto.response.product;

import com.example.hanaro.entity.ProductSalesStats;
import lombok.Getter;


@Getter
public class ProductStatsResponseDto {
    private final Long productId;
    private final String productName;
    private final Integer totalQuantity;
    private final Long totalAmount;

    public ProductStatsResponseDto(ProductSalesStats stats) {
        this.productId = stats.getProductId();
        this.productName = stats.getProductName();
        this.totalQuantity = stats.getTotalQuantity();
        this.totalAmount = stats.getTotalAmount();
    }

    public ProductStatsResponseDto(Long productId, String productName, int totalQuantity, long totalAmount) {
        this.productId = productId;
        this.productName = productName;
        this.totalQuantity = totalQuantity;
        this.totalAmount = totalAmount;
    }
}