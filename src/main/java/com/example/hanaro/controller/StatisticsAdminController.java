package com.example.hanaro.controller;

import com.example.hanaro.dto.response.DailyStatsResponseDto;
import com.example.hanaro.dto.response.product.ProductStatsResponseDto;
import com.example.hanaro.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Admin - Statistics", description = "관리자 통계 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/stats")
@PreAuthorize("hasRole('ADMIN')")
public class StatisticsAdminController {

    private final StatisticsService statisticsService;

    @Operation(summary = "일별 매출 통계 조회", description = "지정된 기간 동안의 일별 매출 및 주문 수를 조회합니다.")
    @GetMapping("/daily-sales")
    public ResponseEntity<List<DailyStatsResponseDto>> getDailySales(
            @Parameter(description = "시작일 (YYYY-MM-DD)", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "종료일 (YYYY-MM-DD)", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<DailyStatsResponseDto> dailyStats = statisticsService.getDailySales(startDate, endDate);
        return ResponseEntity.ok(dailyStats);
    }

    @Operation(summary = "상품 판매 순위 조회", description = "지정된 기간 동안 가장 많이 팔린 상품 순위를 조회합니다.")
    @GetMapping("/product-rank")
    public ResponseEntity<List<ProductStatsResponseDto>> getProductSalesRank(
            @Parameter(description = "시작일 (YYYY-MM-DD)", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "종료일 (YYYY-MM-DD)", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<ProductStatsResponseDto> productRanks = statisticsService.getProductSalesRank(startDate, endDate);
        return ResponseEntity.ok(productRanks);
    }
}
