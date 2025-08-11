package com.example.hanaro.repository;

import com.example.hanaro.entity.DailySalesStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailySalesStatsRepository extends JpaRepository<DailySalesStats, Long> {
    Optional<Object> findByStatsDate(LocalDate yesterday);
    @Query("SELECT d FROM DailySalesStats d WHERE d.statsDate BETWEEN :startDate AND :endDate ORDER BY d.statsDate ASC")
    List<DailySalesStats> findByStatsDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
