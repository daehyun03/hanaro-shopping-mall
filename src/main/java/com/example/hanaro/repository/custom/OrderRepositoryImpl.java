package com.example.hanaro.repository.custom;

import com.example.hanaro.entity.Order;
import com.example.hanaro.entity.QOrder;
import com.example.hanaro.entity.QOrderItem;
import com.example.hanaro.entity.QUser;
import com.example.hanaro.enums.OrderStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Order> findOrdersByCriteria(String userEmail, OrderStatus orderStatus, Long productId) {
        QOrder order = QOrder.order;
        QUser user = QUser.user;
        QOrderItem orderItem = QOrderItem.orderItem;

        return queryFactory
                .selectFrom(order)
                .join(order.user, user).fetchJoin()
                .leftJoin(order.orderItems, orderItem)
                .where(
                        userEmailEq(userEmail),     // 이메일 조건
                        orderStatusEq(orderStatus),  // 주문 상태 조건
                        productIdEq(productId) // 상품 ID 조건 추가
                )
                .orderBy(order.createdAt.desc())
                .fetch();
    }

    // 사용자 이메일 검색 조건 (null이 아니면 조건 추가)
    private BooleanExpression userEmailEq(String userEmail) {
        return userEmail != null ? QUser.user.email.contains(userEmail) : null;
    }

    // 주문 상태 검색 조건 (null이 아니면 조건 추가)
    private BooleanExpression orderStatusEq(OrderStatus orderStatus) {
        return orderStatus != null ? QOrder.order.status.eq(orderStatus) : null;
    }

    // 상품 ID 검색 조건 (null이 아니면 조건 추가)
    private BooleanExpression productIdEq(Long productId) {
        return productId != null ? QOrderItem.orderItem.product.id.eq(productId) : null;
    }
}
