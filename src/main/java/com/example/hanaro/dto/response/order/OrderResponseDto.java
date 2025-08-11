package com.example.hanaro.dto.response.order;

import com.example.hanaro.entity.Order;
import com.example.hanaro.enums.OrderStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class OrderResponseDto {
    private final Long orderId;
    private final OrderStatus status;
    private final int totalPrice;
    private final LocalDateTime orderDate;
    private final List<OrderItemResponseDto> items;

    public OrderResponseDto(Order order) {
        this.orderId = order.getId();
        this.status = order.getStatus();
        this.totalPrice = order.getTotalPrice();
        this.orderDate = order.getCreatedAt();
        this.items = order.getOrderItems().stream()
                .map(OrderItemResponseDto::new)
                .collect(Collectors.toList());
    }
}
