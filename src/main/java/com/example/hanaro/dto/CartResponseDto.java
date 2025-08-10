package com.example.hanaro.dto;

import com.example.hanaro.entity.Cart;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class CartResponseDto {
    private final Long cartId;
    private final List<CartItemResponseDto> items;
    private final Integer totalPrice;

    public CartResponseDto(Cart cart) {
        this.cartId = cart.getId();
        this.items = cart.getCartItems().stream()
                .map(CartItemResponseDto::new)
                .collect(Collectors.toList());
        this.totalPrice = calculateTotalPrice();
    }

    private int calculateTotalPrice() {
        return items.stream()
                .mapToInt(item -> item.getPrice() * item.getQuantity())
                .sum();
    }
}
