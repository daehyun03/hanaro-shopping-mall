package com.example.hanaro.repository;

import com.example.hanaro.entity.*;
import com.example.hanaro.enums.OrderStatus;
import com.example.hanaro.enums.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.LongStream;

@SpringBootTest
@Transactional
public class OrderRepositoryTests {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;

    @Test
    @Commit
    @DisplayName("매출 통계용 주문 데이터 30개 생성")
    void createInitialOrders() {
        List<User> users = userRepository.findAll();
        users.removeIf(user -> user.getRole().equals(UserRole.ROLE_ADMIN));

        List<Product> products = productRepository.findAll();

        if (users.isEmpty() || products.isEmpty()) {
            System.out.println("필요한 사용자 또는 상품 데이터가 DB에 없습니다.");
            return;
        }

        Random random = new Random();
        List<Order> newOrders = new ArrayList<>();

        for (int i = 0; i < 30; i++) {
            User randomUser = users.get(random.nextInt(users.size()));

            Order order = Order.builder()
                    .user(randomUser)
                    .status(OrderStatus.DELIVERED)
                    .build();

            int itemCount = random.nextInt(3) + 1;
            List<OrderItem> orderItems = new ArrayList<>();
            int totalPrice = 0;

            for (int j = 0; j < itemCount; j++) {
                Product randomProduct = products.get(random.nextInt(products.size()));
                int quantity = random.nextInt(5) + 1;

                orderItems.add(OrderItem.builder()
                        .order(order)
                        .product(randomProduct)
                        .orderPrice(randomProduct.getPrice())
                        .quantity(quantity)
                        .build());

                totalPrice += randomProduct.getPrice() * quantity;
            }

            order.setOrderItems(orderItems);
            order.setTotalPrice(totalPrice);

            LocalDateTime orderDate = LocalDateTime.now();
            ReflectionTestUtils.setField(order, "createdAt", orderDate);

            newOrders.add(order);
        }

        orderRepository.saveAll(newOrders);
        System.out.println("주문 데이터 생성 완료. 총 " + orderRepository.count() + "개");
    }
}
