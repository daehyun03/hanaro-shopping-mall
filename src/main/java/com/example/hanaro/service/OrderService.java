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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
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

        String stockChanges = newOrder.getOrderItems().stream()
                .map(item -> String.format("productId=%d, 변경량=-%d", item.getProduct().getId(), item.getQuantity()))
                .collect(Collectors.joining(", "));
        log.info("장바구니에서 주문이 생성되었습니다. 주문 ID: {}, 사용자: {}", newOrder.getId(), userEmail);
        log.info("주문 생성으로 재고가 변경되었습니다. orderId={}, 변경내역=[{}]", newOrder.getId(), stockChanges);
        return new OrderResponseDto(newOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDto> getMyOrders(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        List<Order> orders = orderRepository.findAllByUserOrderByCreatedAtDesc(user);
        log.info("주문 조회 완료. 사용자: {}, 주문 개수: {}", userEmail, orders.size());
        return orders.stream()
                .map(OrderResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDto> getAllOrders(String userEmail, OrderStatus orderStatus, Long productId) {
        List<Order> orders = orderRepository.findOrdersByCriteria(userEmail, orderStatus, productId);
        log.info("주문 조회 완료. 사용자: {}, 주문 상태: {}, 상품 아이디: {}, 주문 개수: {}", userEmail, orderStatus, productId, orders.size());
        return orders.stream()
                .map(OrderResponseDto::new)
                .collect(Collectors.toList());
    }
}
