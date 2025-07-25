package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.ImportOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ImportOrderRepository extends JpaRepository<ImportOrder, Integer> {

    //Chart thông tin nhập kho
    @Query(value = """
    SELECT 
        TO_CHAR(io.created_at, 'YYYY-MM-DD') AS import_date,
        COUNT(DISTINCT io.id) AS total_orders,
        SUM(od.quantity) AS total_items
    FROM importorder io
    JOIN importorderdetail od ON io.id = od.import_order_id
    JOIN sku s ON od.sku_id = s.id
    JOIN box b ON b.sku_id = s.id
    JOIN bin bi ON b.bin_id = bi.id
    JOIN shelf sh ON bi.shelf_id = sh.id
    JOIN warehouse w ON sh.warehouse_id = w.id
    WHERE (:warehouseId IS NULL OR w.id = :warehouseId)
      AND io.created_at BETWEEN TO_DATE(:fromDate, 'YYYY-MM-DD') AND TO_DATE(:toDate, 'YYYY-MM-DD')
    GROUP BY TO_CHAR(io.created_at, 'YYYY-MM-DD')
    ORDER BY TO_CHAR(io.created_at, 'YYYY-MM-DD')
""", nativeQuery = true)
    List<Object[]> getImportChartData(
            @Param("warehouseId") Integer warehouseId,
            @Param("fromDate") String fromDate,
            @Param("toDate") String toDate
    );

    //Chart thông tin tổng kết+Chỉ số tối ưu hoá nhập – xuất
    @Query(value = """
    SELECT 
        DATE(io.created_at) AS stat_date,
        COUNT(DISTINCT io.id) AS total_import_orders,
        COALESCE(SUM(iod.quantity), 0) AS total_import_items
    FROM importorder io
    LEFT JOIN importorderdetail iod ON io.id = iod.import_order_id
    WHERE (:warehouseId IS NULL OR EXISTS (
        SELECT 1
        FROM box b
        JOIN bin bi ON b.bin_id = bi.id
        JOIN shelf s ON bi.shelf_id = s.id
        JOIN warehouse w ON s.warehouse_id = w.id
        WHERE b.sku_id = iod.sku_id AND w.id = :warehouseId
    ))
    AND io.created_at BETWEEN :startDate AND :endDate
    GROUP BY stat_date
    ORDER BY stat_date
""", nativeQuery = true)
    List<Object[]> getImportStatistics(
            @Param("warehouseId") Integer warehouseId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

    //Tìm kiếm đơn nhập
    @Query("""
        SELECT io FROM ImportOrder io
        WHERE (:source IS NULL OR io.source = :source)
          AND (:status IS NULL OR io.status = :status)
          AND (:createdBy IS NULL OR io.createdBy.username = :createdBy)
          AND (:startDate IS NULL OR io.createdAt >= :startDate)
          AND (:endDate IS NULL OR io.createdAt <= :endDate)
    """)
    List<ImportOrder> searchImportOrders(
            @Param("source") com.example.warehousesystem.entity.ImportOrder.Source source,
            @Param("status") com.example.warehousesystem.entity.ImportOrder.Status status,
            @Param("createdBy") String createdBy,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

}
