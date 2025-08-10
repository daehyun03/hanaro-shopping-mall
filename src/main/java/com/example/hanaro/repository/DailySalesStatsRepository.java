package com.example.hanaro.repository;

import com.example.hanaro.entity.DailySalesStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface DailySalesStatsRepository extends JpaRepository<DailySalesStats, Long> {
    Optional<Object> findByStatsDate(LocalDate yesterday);
}
