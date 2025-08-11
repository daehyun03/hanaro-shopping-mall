package com.example.hanaro.dto.response.product;

import com.example.hanaro.entity.Product;
import lombok.Getter;

@Getter
public class ProductResponseDto {

    private final Long id;
    private final String name;
    private final Integer price;
    private final String thumbnailUrl;  // 썸네일 이미지 URL

    public ProductResponseDto(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.thumbnailUrl = generateThumbnailUrl(product.getImageUrl());
    }

    private String generateThumbnailUrl(String originalUrl) {
        if (originalUrl == null || originalUrl.isEmpty()) {
            return null;
        }
        int lastSlashIndex = originalUrl.lastIndexOf('/');
        String path = originalUrl.substring(0, lastSlashIndex + 1);
        String filename = originalUrl.substring(lastSlashIndex + 1);
        return "/" + path + "s_" + filename;
    }
}
