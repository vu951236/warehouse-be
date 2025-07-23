package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.SKUPickingFrequency;
import com.example.warehousesystem.entity.SKU;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SKUPickingFrequencyRepository extends JpaRepository<SKUPickingFrequency, Integer> {

    // Lấy danh sách SKU được lấy nhiều nhất trong khoảng thời gian
    @Query("SELECT spf.sku FROM SKUPickingFrequency spf WHERE spf.periodStart >= :start AND spf.periodEnd <= :end ORDER BY spf.pickCount DESC")
    List<SKU> findTopPickedSKUsBetween(LocalDate start, LocalDate end);
}
