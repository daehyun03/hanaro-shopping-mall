package com.example.hanaro.service;

import com.example.hanaro.dto.DailyStatsResponseDto;
import com.example.hanaro.dto.ProductStatsResponseDto;
import com.example.hanaro.entity.DailySalesStats;
import com.example.hanaro.entity.ProductSalesStats;
import com.example.hanaro.repository.DailySalesStatsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsService {

    private final DailySalesStatsRepository dailySalesStatsRepository;

    // 일별 매출 통계 조회
    public List<DailyStatsResponseDto> getDailySales(LocalDate startDate, LocalDate endDate) {
        List<DailySalesStats> stats = dailySalesStatsRepository.findByStatsDateBetween(startDate, endDate);
        return stats.stream()
                .map(DailyStatsResponseDto::new)
                .collect(Collectors.toList());
    }

    // 기간 내 상품 판매 순위 조회
    public List<ProductStatsResponseDto> getProductSalesRank(LocalDate startDate, LocalDate endDate) {
        List<DailySalesStats> stats = dailySalesStatsRepository.findByStatsDateBetween(startDate, endDate);

        return stats.stream()
                .flatMap(dailyStat -> dailyStat.getProductSalesStats().stream())
                .collect(Collectors.groupingBy(
                        ProductSalesStats::getProductId,
                        Collectors.collectingAndThen(Collectors.toList(), list -> {
                            ProductStatsResponseDto dto = new ProductStatsResponseDto(list.getFirst());
                            int totalQuantity = list.stream().mapToInt(ProductSalesStats::getTotalQuantity).sum();
                            long totalAmount = list.stream().mapToLong(ProductSalesStats::getTotalAmount).sum();
                            // 합산된 값으로 새로운 DTO 생성
                            return new ProductStatsResponseDto(
                                    dto.getProductId(), dto.getProductName(), totalQuantity, totalAmount
                            );
                        })
                ))
                .values().stream()
                .sorted(Comparator.comparing(ProductStatsResponseDto::getTotalAmount).reversed())
                .collect(Collectors.toList());
    }
}
