package com.example.hanaro.dto.response.product;

import com.example.hanaro.entity.Product;
import lombok.Getter;

@Getter
public class ProductDetailResponseDto {
    private final Long id;
    private final String name;
    private final Integer price;
    private final Integer stock;
    private final String imageUrl;

    public ProductDetailResponseDto(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.stock = product.getStock();
        this.imageUrl = product.getImageUrl();
    }
}
