package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.SKU;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SKURepository extends JpaRepository<SKU, Integer> {
    //Chart Tỉ lệ loại hàng
    @Query(value = """
    SELECT 
        s.name AS sku_name,
        COUNT(i.id) AS quantity
    FROM item i
    JOIN sku s ON i.sku_id = s.id
    WHERE i.status = 'available'
    GROUP BY s.name
    ORDER BY quantity DESC
    """, nativeQuery = true)
    List<Object[]> getCurrentStockRatioChart();

}
