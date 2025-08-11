package com.example.hanaro.dto;

import com.example.hanaro.entity.DailySalesStats;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class DailyStatsResponseDto {
    private final LocalDate date;
    private final Long totalSalesAmount;
    private final Integer totalOrderCount;

    public DailyStatsResponseDto(DailySalesStats stats) {
        this.date = stats.getStatsDate();
        this.totalSalesAmount = stats.getTotalSalesAmount();
        this.totalOrderCount = stats.getTotalOrderCount();
    }
}