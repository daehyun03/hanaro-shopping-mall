package com.example.hanaro.dto.response.cart;

import com.example.hanaro.entity.CartItem;
import lombok.Getter;

@Getter
public class CartItemResponseDto {
    private final Long cartItemId;
    private final Long productId;
    private final String productName;
    private final Integer price;
    private final Integer quantity;
    private final String thumbnailUrl;

    public CartItemResponseDto(CartItem cartItem) {
        this.cartItemId = cartItem.getId();
        this.productId = cartItem.getProduct().getId();
        this.productName = cartItem.getProduct().getName();
        this.price = cartItem.getProduct().getPrice();
        this.quantity = cartItem.getQuantity();
        this.thumbnailUrl = generateThumbnailUrl(cartItem.getProduct().getImageUrl());
    }

    private String generateThumbnailUrl(String originalUrl) {
        if (originalUrl == null || originalUrl.isEmpty()) { return null; }
        int lastSlashIndex = originalUrl.lastIndexOf('/');
        String path = originalUrl.substring(0, lastSlashIndex + 1);
        String filename = originalUrl.substring(lastSlashIndex + 1);
        return "/" + path + "s_" + filename;
    }
}
