package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.SKUPickingFrequency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SKUPickingFrequencyRepository extends JpaRepository<SKUPickingFrequency, Integer> {
    //[ Thuật toán] Phân bổ lại vị trí SKU theo độ picking hằng tháng
    @Query("""
        SELECT f FROM SKUPickingFrequency f
        JOIN FETCH f.sku s
        WHERE f.periodStart = :startDate AND f.periodEnd = :endDate
        ORDER BY f.pickCount DESC
    """)
    List<SKUPickingFrequency> findTopByPickCount(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
