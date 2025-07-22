package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.SKUPickingFrequency;
import com.example.warehousesystem.entity.SKU;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SKUPickingFrequencyRepository extends JpaRepository<SKUPickingFrequency, Integer> {

    List<SKUPickingFrequency> findBySku(SKU sku);

    Optional<SKUPickingFrequency> findBySkuAndPeriodStartAndPeriodEnd(SKU sku, LocalDate periodStart, LocalDate periodEnd);

    List<SKUPickingFrequency> findByPeriodStartGreaterThanEqualAndPeriodEndLessThanEqual(LocalDate start, LocalDate end);
}
