package com.example.hanaro.entity;

import com.example.hanaro.exception.ErrorCode;
import com.example.hanaro.exception.CustomException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private Integer stock;

    private String imageUrl;

    public void updateStock(Integer stock) {
        this.stock = stock;
    }

    public void updateDetails(String name, int price, int stock, String imageUrl) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        // 새로운 이미지 경로가 null이 아닐 경우에만 업데이트
        if (imageUrl != null) {
            this.imageUrl = imageUrl;
        }
    }

    public void decreaseStock(int quantity) {
        if (this.stock - quantity < 0) {
            throw new CustomException(ErrorCode.STOCK_NOT_ENOUGH);
        }
        this.stock -= quantity;
    }
}
