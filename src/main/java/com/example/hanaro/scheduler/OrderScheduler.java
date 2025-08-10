package com.example.hanaro.scheduler;

import com.example.hanaro.entity.Order;
import com.example.hanaro.enums.OrderStatus;
import com.example.hanaro.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderScheduler {

    private final OrderRepository orderRepository;

    /**
     * 결제 완료 -> 배송 준비 (매 5분마다 실행)
     */
    @Scheduled(cron = "0 */5 * * * *")
    @Transactional
    public void prepareOrders() {
        List<Order> completedOrders = orderRepository.findAllByStatus(OrderStatus.PAYMENT_COMPLETED);
        if (completedOrders.isEmpty()) {
            return;
        }

        for (Order order : completedOrders) {
            order.updateStatus(OrderStatus.PREPARING);
        }
        log.info("[스케줄러] {}개의 주문이 '배송 준비' 상태로 변경되었습니다.", completedOrders.size());
    }

    /**
     * 배송 준비 -> 배송 중 (매 15분마다 실행)
     */
    @Scheduled(cron = "0 */15 * * * *")
    @Transactional
    public void shipOrders() {
        List<Order> preparingOrders = orderRepository.findAllByStatus(OrderStatus.PREPARING);
        if (preparingOrders.isEmpty()) {
            return;
        }

        for (Order order : preparingOrders) {
            order.updateStatus(OrderStatus.SHIPPING);
        }
        log.info("[스케줄러] {}개의 주문이 '배송 중' 상태로 변경되었습니다.", preparingOrders.size());
    }

    /**
     * 배송 중 -> 배송 완료 (매 1시간마다 실행)
     */
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void deliverOrders() {
        List<Order> shippingOrders = orderRepository.findAllByStatus(OrderStatus.SHIPPING);
        if (shippingOrders.isEmpty()) {
            return;
        }

        for (Order order : shippingOrders) {
            order.updateStatus(OrderStatus.DELIVERED);
        }
        log.info("[스케줄러] {}개의 주문이 '배송 완료' 상태로 변경되었습니다.", shippingOrders.size());
    }
}