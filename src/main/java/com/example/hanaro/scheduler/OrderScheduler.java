package com.example.hanaro.scheduler;

import com.example.hanaro.entity.Order;
import com.example.hanaro.enums.OrderStatus;
import com.example.hanaro.repository.OrderRepository;
import com.example.hanaro.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderScheduler {

    private final OrderRepository orderRepository;
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    /**
     * [상태 변경] 결제 완료 -> 배송 준비
     * 매 분마다 실행하여, 생성된 지 5분이 지난 '결제 완료' 주문을 '배송 준비'로 변경
     */
    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void prepareOrders() {
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);
        List<Order> targetOrders = orderRepository.findAllByStatusAndCreatedAtBefore(
                OrderStatus.PAYMENT_COMPLETED,
                fiveMinutesAgo
        );
        if (targetOrders.isEmpty()) {
            return;
        }

        for (Order order : targetOrders) {
            order.updateStatus(OrderStatus.PREPARING);
        }
        log.info("[스케줄러] {}개의 주문이 '배송 준비' 상태로 변경되었습니다.", targetOrders.size());
    }

    /**
     * [상태 변경] 배송 준비 -> 배송 중
     * 매 분마다 실행하여, 생성된 지 15분이 지난 '배송 준비' 주문을 '배송 중'으로 변경
     */
    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void shipOrders() {
        LocalDateTime fifteenMinutesAgo = LocalDateTime.now().minusMinutes(15);
        List<Order> targetOrders = orderRepository.findAllByStatusAndCreatedAtBefore(
                OrderStatus.PREPARING,
                fifteenMinutesAgo
        );
        if (targetOrders.isEmpty()) {
            return;
        }
        for (Order order : targetOrders) {
            order.updateStatus(OrderStatus.SHIPPING);
        }
        log.info("[스케줄러] {}개의 주문이 '배송 중' 상태로 변경되었습니다.", targetOrders.size());
    }

    /**
     * [상태 변경] 배송 중 -> 배송 완료
     * 매 분마다 실행하여, 생성된 지 60분이 지난 '배송 중' 주문을 '배송 완료'로 변경
     */
    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void deliverOrders() {
        LocalDateTime sixtyMinutesAgo = LocalDateTime.now().minusMinutes(60);
        List<Order> targetOrders = orderRepository.findAllByStatusAndCreatedAtBefore(
                OrderStatus.SHIPPING,
                sixtyMinutesAgo
        );
        if (targetOrders.isEmpty()) {
            return;
        }
        for (Order order : targetOrders) {
            order.updateStatus(OrderStatus.DELIVERED);
        }
        log.info("[스케줄러] {}개의 주문이 '배송 완료' 상태로 변경되었습니다.", targetOrders.size());
    }
}