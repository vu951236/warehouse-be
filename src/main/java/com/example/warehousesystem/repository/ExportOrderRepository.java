package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.ExportOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.awt.print.Pageable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ExportOrderRepository extends JpaRepository<ExportOrder, Integer> {
    Optional<ExportOrder> findByExportCode(String exportCode);

    // 1. KPI - Tổng số đơn xuất confirmed
    @Query("""
        SELECT COUNT(DISTINCT eo.id)
        FROM ExportOrder eo
        WHERE eo.status = com.example.warehousesystem.entity.ExportOrder.Status.confirmed
          AND (CAST(:warehouseId AS integer) IS NULL OR eo.createdBy.warehouse.id = :warehouseId)
          AND eo.createdAt BETWEEN :fromDate AND :toDate
    """)
    Long countConfirmedOrders(
            @Param("warehouseId") Integer warehouseId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    // 2. KPI - Tổng số item xuất
    @Query("""
        SELECT COALESCE(SUM(eod.quantity), 0)
        FROM ExportOrder eo
        JOIN ExportOrderDetail eod ON eo.id = eod.exportOrder.id
        WHERE eo.status = com.example.warehousesystem.entity.ExportOrder.Status.confirmed
          AND (CAST(:warehouseId AS integer) IS NULL OR eo.createdBy.warehouse.id = :warehouseId)
          AND eo.createdAt BETWEEN :fromDate AND :toDate
    """)
    Long sumConfirmedItems(
            @Param("warehouseId") Integer warehouseId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    // 3. Biểu đồ xuất hàng (stacked column chart)
    @Query("""
        SELECT eo.createdAt AS exportDate,
               COUNT(DISTINCT eo.id) AS totalOrders,
               COALESCE(SUM(eod.quantity), 0) AS totalItems,
               SUM(CASE WHEN eo.source = com.example.warehousesystem.entity.ExportOrder.Source.manual THEN eod.quantity ELSE 0 END) AS manualItems,
               SUM(CASE WHEN eo.source = com.example.warehousesystem.entity.ExportOrder.Source.haravan THEN eod.quantity ELSE 0 END) AS haravanItems
        FROM ExportOrder eo
        JOIN ExportOrderDetail eod ON eo.id = eod.exportOrder.id
        WHERE eo.status = com.example.warehousesystem.entity.ExportOrder.Status.confirmed
          AND (CAST(:warehouseId AS integer) IS NULL OR eo.createdBy.warehouse.id = :warehouseId)
          AND eo.createdAt BETWEEN :fromDate AND :toDate
        GROUP BY eo.createdAt
        ORDER BY eo.createdAt
    """)
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
}
