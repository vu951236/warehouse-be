package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.ExportOrder;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.awt.print.Pageable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ExportOrderRepository extends JpaRepository<ExportOrder, Integer> {
    Optional<ExportOrder> findByExportCode(String exportCode);

    // 1. Tổng số đơn confirmed
    @Query(value = """
    SELECT COUNT(DISTINCT eo.id)
    FROM exportorder eo
    JOIN exportorderdetail eod ON eo.id = eod.export_order_id
    JOIN sku s ON eod.sku_id = s.id
    JOIN box b ON b.sku_id = s.id
    JOIN bin bi ON b.bin_id = bi.id
    JOIN shelf sh ON bi.shelf_id = sh.id
    JOIN warehouse w ON sh.warehouse_id = w.id
    WHERE eo.status = 'confirmed'
      AND (:warehouseId IS NULL OR w.id = :warehouseId)
      AND DATE(eo.created_at) BETWEEN :fromDate AND :toDate
""", nativeQuery = true)
    Long countConfirmedExportOrders(
            @Param("warehouseId") Integer warehouseId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    // 2. Tổng số lượng confirmed
    @Query(value = """
    SELECT COALESCE(SUM(eod.quantity), 0)
    FROM exportorder eo
    JOIN exportorderdetail eod ON eo.id = eod.export_order_id
    JOIN sku s ON eod.sku_id = s.id
    JOIN box b ON b.sku_id = s.id
    JOIN bin bi ON b.bin_id = bi.id
    JOIN shelf sh ON bi.shelf_id = sh.id
    JOIN warehouse w ON sh.warehouse_id = w.id
    WHERE eo.status = 'confirmed'
      AND (:warehouseId IS NULL OR w.id = :warehouseId)
      AND DATE(eo.created_at) BETWEEN :fromDate AND :toDate
""", nativeQuery = true)
    Long sumConfirmedExportQuantity(
            @Param("warehouseId") Integer warehouseId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    // 3. Chart Data
    @Query(value = """
    SELECT 
        DATE(eo.created_at) AS export_date,
        SUM(CASE WHEN eo.source = 'manual' THEN eod.quantity ELSE 0 END) AS manual_quantity,
        SUM(CASE WHEN eo.source = 'haravan' THEN eod.quantity ELSE 0 END) AS haravan_quantity
    FROM exportorder eo
    JOIN exportorderdetail eod ON eo.id = eod.export_order_id
    JOIN sku s ON eod.sku_id = s.id
    JOIN box b ON b.sku_id = s.id
    JOIN bin bi ON b.bin_id = bi.id
    JOIN shelf sh ON bi.shelf_id = sh.id
    JOIN warehouse w ON sh.warehouse_id = w.id
    WHERE eo.status = 'confirmed'
      AND (:warehouseId IS NULL OR w.id = :warehouseId)
      AND DATE(eo.created_at) BETWEEN :fromDate AND :toDate
    GROUP BY DATE(eo.created_at)
    ORDER BY export_date
""", nativeQuery = true)
    List<Object[]> getExportChartData(
            @Param("warehouseId") Integer warehouseId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
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

    //Tìm kiếm đơn xuất
    @Query("""
        SELECT eo FROM ExportOrder eo
        WHERE (:source IS NULL OR eo.source = :source)
          AND (:status IS NULL OR eo.status = :status)
          AND (:createdBy IS NULL OR eo.createdBy.username = :createdBy)
          AND (:startDate IS NULL OR eo.createdAt >= :startDate)
          AND (:endDate IS NULL OR eo.createdAt <= :endDate)
    """)
    List<ExportOrder> searchExportOrders(
            @Param("source") com.example.warehousesystem.entity.ExportOrder.Source source,
            @Param("status") com.example.warehousesystem.entity.ExportOrder.Status status,
            @Param("createdBy") String createdBy,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    //[Thuật toán] Ưu tiên đơn hàng gấp
    @Query("""
    SELECT eo FROM ExportOrder eo
    WHERE eo.status = :status
    ORDER BY eo.createdAt ASC
""")
    List<ExportOrder> findUrgentOrders(@Param("status") ExportOrder.Status status, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE ExportOrder e SET e.note = :note WHERE e.exportCode = :exportCode")
    int updateNoteByExportCode(@Param("exportCode") String exportCode, @Param("note") String note);

}
