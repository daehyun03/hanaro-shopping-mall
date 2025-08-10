package com.example.hanaro.repository.custom;

import com.example.hanaro.entity.Order;
import com.example.hanaro.enums.OrderStatus;

import java.util.List;

public interface OrderRepositoryCustom {
    List<Order> findOrdersByCriteria(String userEmail, OrderStatus orderStatus);
}
