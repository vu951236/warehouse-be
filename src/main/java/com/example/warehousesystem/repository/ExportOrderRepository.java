package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.ExportOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExportOrderRepository extends JpaRepository<ExportOrder, Integer> {

    // Chart thông tin xuất kho
    @Query(value = """
    SELECT 
        TO_CHAR(eo.created_at, 'YYYY-MM-DD') AS export_date,
        COUNT(DISTINCT eo.id) AS total_orders,
        SUM(eod.quantity) AS total_items
    FROM exportorder eo
    JOIN exportorderdetail eod ON eo.id = eod.export_order_id
    JOIN sku s ON eod.sku_id = s.id
    JOIN box b ON b.sku_id = s.id
    JOIN bin bi ON b.bin_id = bi.id
    JOIN shelf sh ON bi.shelf_id = sh.id
    JOIN warehouse w ON sh.warehouse_id = w.id
    WHERE (:warehouseId IS NULL OR w.id = :warehouseId)
      AND eo.created_at BETWEEN TO_DATE(:fromDate, 'YYYY-MM-DD') AND TO_DATE(:toDate, 'YYYY-MM-DD')
    GROUP BY TO_CHAR(eo.created_at, 'YYYY-MM-DD')
    ORDER BY TO_CHAR(eo.created_at, 'YYYY-MM-DD')
""", nativeQuery = true)
    List<Object[]> getExportChartData(
            @Param("warehouseId") Integer warehouseId,
            @Param("fromDate") String fromDate,
            @Param("toDate") String toDate
    );

    // Chart thông tin tổng kết+Chỉ số tối ưu hoá nhập – xuất
    @Query(value = """
    SELECT 
        DATE(eo.created_at) AS export_date,
        COUNT(DISTINCT eo.id) AS total_export_orders,
        COALESCE(SUM(eod.quantity), 0) AS total_export_items
    FROM exportorder eo
    LEFT JOIN exportorderdetail eod ON eo.id = eod.export_order_id
    WHERE (:warehouseId IS NULL OR EXISTS (
        SELECT 1
        FROM box b
        JOIN bin bi ON b.bin_id = bi.id
        JOIN shelf s ON bi.shelf_id = s.id
        JOIN warehouse w ON s.warehouse_id = w.id
        WHERE b.sku_id = eod.sku_id AND w.id = :warehouseId
    ))
    AND eo.created_at BETWEEN :startDate AND :endDate
    GROUP BY export_date
    ORDER BY export_date
""", nativeQuery = true)
    List<Object[]> getExportStatistics(
            @Param("warehouseId") Integer warehouseId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

}
