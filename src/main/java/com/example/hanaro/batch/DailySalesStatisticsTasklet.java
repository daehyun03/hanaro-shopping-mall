package com.example.hanaro.batch;

import com.example.hanaro.entity.DailySalesStats;
import com.example.hanaro.entity.Order;
import com.example.hanaro.entity.OrderItem;
import com.example.hanaro.entity.ProductSalesStats;
import com.example.hanaro.enums.OrderStatus;
import com.example.hanaro.repository.DailySalesStatsRepository;
import com.example.hanaro.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class DailySalesStatisticsTasklet implements Tasklet {

    private final OrderRepository orderRepository;
    private final DailySalesStatsRepository dailySalesStatsRepository;

    @Override
    @Transactional
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        if (dailySalesStatsRepository.findByStatsDate(yesterday).isPresent()) {
            log.info("어제({}) 날짜의 매출 통계가 이미 존재하므로 배치를 건너뜁니다.", yesterday);
            return RepeatStatus.FINISHED;
        }
        log.info("[배치 Job] 어제({}) 날짜의 매출 통계 집계를 시작합니다.", yesterday);

        // 어제 날짜의 시작과 끝 시간 정의
        LocalDateTime startOfDay = yesterday.atStartOfDay();
        LocalDateTime endOfDay = yesterday.atTime(LocalTime.MAX);

        // 어제 날짜의 '배송 완료'된 모든 주문을 조회
        List<Order> completedOrders = orderRepository.findAllByStatusAndCreatedAtBetween(
                OrderStatus.DELIVERED, startOfDay, endOfDay);

        if (completedOrders.isEmpty()) {
            log.info("어제({}) 날짜에 배송 완료된 주문이 없어 통계를 생성하지 않습니다.", yesterday);
            return RepeatStatus.FINISHED;
        }

        // 주문 정보를 바탕으로 통계 집계
        long totalSalesAmount = completedOrders.stream().mapToLong(Order::getTotalPrice).sum();
        int totalOrderCount = completedOrders.size();

        // 상품별 통계 집계
        Map<Long, ProductSalesStats> productStatsMap = completedOrders.stream()
                .flatMap(order -> order.getOrderItems().stream())
                .collect(Collectors.groupingBy(
                        orderItem -> orderItem.getProduct().getId(),
                        Collectors.collectingAndThen(Collectors.toList(), list -> {
                            OrderItem firstItem = list.get(0);
                            int totalQuantity = list.stream().mapToInt(OrderItem::getQuantity).sum();
                            long totalAmount = (long) firstItem.getOrderPrice() * totalQuantity;
                            return ProductSalesStats.builder()
                                    .productId(firstItem.getProduct().getId())
                                    .productName(firstItem.getProduct().getName())
                                    .totalQuantity(totalQuantity)
                                    .totalAmount(totalAmount)
                                    .build();
                        })
                ));

        // 집계된 통계를 DB에 저장
        DailySalesStats stats = DailySalesStats.builder()
                .statsDate(yesterday)
                .totalSalesAmount(totalSalesAmount)
                .totalOrderCount(totalOrderCount)
                .build();

        // 상품별 통계에 일별 통계와의 관계 설정
        productStatsMap.values().forEach(productStat -> productStat.setDailySalesStats(stats));
        stats.setProductSalesStats(new ArrayList<>(productStatsMap.values()));

        dailySalesStatsRepository.save(stats);

        log.info("[배치 Job] 어제({}) 날짜의 매출 통계가 성공적으로 저장되었습니다.", yesterday);
        return RepeatStatus.FINISHED;
    }
}