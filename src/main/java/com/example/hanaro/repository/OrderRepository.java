package com.example.hanaro.repository;

import com.example.hanaro.entity.Order;
import com.example.hanaro.entity.User;
import com.example.hanaro.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.hanaro.repository.custom.OrderRepositoryCustom;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long>, OrderRepositoryCustom {
    List<Order> findAllByStatus(OrderStatus status);
    List<Order> findAllByUserOrderByCreatedAtDesc(User user);
    List<Order> findAllByOrderByCreatedAtDesc();
}
