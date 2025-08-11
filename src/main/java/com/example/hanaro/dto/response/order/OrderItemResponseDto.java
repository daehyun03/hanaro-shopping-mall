package com.example.hanaro.dto.response.order;

import com.example.hanaro.entity.OrderItem;
import lombok.Getter;

@Getter
public class OrderItemResponseDto {
    private final Long productId;
    private final String productName;
    private final int quantity;
    private final int orderPrice;

    public OrderItemResponseDto(OrderItem orderItem) {
        this.productId = orderItem.getProduct().getId();
        this.productName = orderItem.getProduct().getName();
        this.quantity = orderItem.getQuantity();
        this.orderPrice = orderItem.getOrderPrice();
    }
}
