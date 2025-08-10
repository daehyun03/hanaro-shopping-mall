package com.example.hanaro.service;

import com.example.hanaro.dto.OrderResponseDto;
import com.example.hanaro.entity.*;
import com.example.hanaro.enums.ErrorCode;
import com.example.hanaro.enums.OrderStatus;
import com.example.hanaro.exception.CustomException;
import com.example.hanaro.repository.CartItemRepository;
import com.example.hanaro.repository.CartRepository;
import com.example.hanaro.repository.OrderRepository;
import com.example.hanaro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class OrderService {
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;

    public OrderResponseDto createOrderFromCart(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 사용자의 장바구니를 조회
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new CustomException(ErrorCode.CART_NOT_FOUND));

        List<CartItem> cartItems = cart.getCartItems();
        if (cartItems.isEmpty()) {
            throw new CustomException(ErrorCode.CART_IS_EMPTY);
        }

        // 재고 확인
        int totalPrice = 0;
        for (CartItem cartItem : cartItems) {
            if (cartItem.getProduct().getStock() < cartItem.getQuantity()) {
                throw new CustomException(ErrorCode.STOCK_NOT_ENOUGH);
            }
            totalPrice += cartItem.getProduct().getPrice() * cartItem.getQuantity();
        }

        // 주문(Order) 생성
        Order newOrder = Order.builder()
                .user(user)
                .totalPrice(totalPrice)
                .status(OrderStatus.PAYMENT_COMPLETED)
                .build();

        // 주문 상품(OrderItem) 생성 및 상품 재고 감소
        List<OrderItem> orderItems = cartItems.stream().map(cartItem -> {
            Product product = cartItem.getProduct();
            product.decreaseStock(cartItem.getQuantity());
            return OrderItem.builder()
                    .order(newOrder)
                    .product(product)
                    .orderPrice(product.getPrice())
                    .quantity(cartItem.getQuantity())
                    .build();
        }).collect(Collectors.toList());
        newOrder.setOrderItems(orderItems);

        // 주문 저장
        orderRepository.save(newOrder);

        // 장바구니 비우기 (해당 장바구니의 모든 CartItem 삭제)
        cart.getCartItems().clear();
        cartItemRepository.deleteAll(cartItems);

        return new OrderResponseDto(newOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDto> getMyOrders(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        List<Order> orders = orderRepository.findAllByUserOrderByCreatedAtDesc(user);
        return orders.stream()
                .map(OrderResponseDto::new)
                .collect(Collectors.toList());
    }
}
