package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.Item;
import com.example.warehousesystem.entity.SKU;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

    //Tìm kiếm SKU
    @Query("""
    SELECT DISTINCT s FROM SKU s
    LEFT JOIN Item i ON i.sku = s
    LEFT JOIN Box b ON b.sku = s
    LEFT JOIN ImportOrderDetail iod ON iod.sku = s
    LEFT JOIN ExportOrderDetail eod ON eod.sku = s
    WHERE (:skuId IS NULL OR s.id = :skuId)
      AND (:itemId IS NULL OR i.id = :itemId)
      AND (:boxId IS NULL OR b.id = :boxId)
      AND (:importOrderId IS NULL OR iod.importOrder.id = :importOrderId)
      AND (:exportOrderId IS NULL OR eod.exportOrder.id = :exportOrderId)
""")
    List<SKU> searchSkuByConditions(
            @Param("skuId") Integer skuId,
            @Param("itemId") Integer itemId,
            @Param("boxId") Integer boxId,
            @Param("importOrderId") Integer importOrderId,
            @Param("exportOrderId") Integer exportOrderId
    );

    //Thêm SKU
    boolean existsBySkuCode(String skuCode);

    @Query("""
    SELECT s FROM SKU s
    WHERE s.skuCode IN :skuCode
""")
    Optional<SKU> findBySkuCode(String skuCode);

    @Query(value = """
    SELECT b.bin_id, GROUP_CONCAT(DISTINCT s.sku_code SEPARATOR ', ')
    FROM box b
    JOIN sku s ON b.sku_id = s.id
    WHERE b.is_deleted = false
    GROUP BY b.bin_id
""", nativeQuery = true)
    List<Object[]> findSkuCodesByBinId();
}
