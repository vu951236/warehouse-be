package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.ExportOrder;
import com.example.warehousesystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ExportOrderRepository extends JpaRepository<ExportOrder, Integer> {

    // Thống kê số đơn xuất theo ngày
    @Query("SELECT DATE(eo.createdAt), COUNT(eo.id) " +
            "FROM ExportOrder eo " +
            "GROUP BY DATE(eo.createdAt) " +
            "ORDER BY DATE(eo.createdAt)")
    List<Object[]> countExportOrdersByDate();

    // Thống kê số đơn xuất theo trạng thái
    @Query("SELECT eo.status, COUNT(eo.id) " +
            "FROM ExportOrder eo " +
            "GROUP BY eo.status")
    List<Object[]> countExportOrdersByStatus();

    // Thống kê số đơn xuất theo nguồn (manual, haravan)
    @Query("SELECT eo.source, COUNT(eo.id) " +
            "FROM ExportOrder eo " +
            "GROUP BY eo.source")
    List<Object[]> countExportOrdersBySource();

    // Đếm số đơn xuất trong khoảng thời gian
    long countByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Tìm theo mã đơn hàng (order code)
    List<ExportOrder> findByOrderCodeContainingIgnoreCase(String orderCode);

    // Tìm theo trạng thái
    List<ExportOrder> findByStatus(ExportOrder.Status status);

    // Tìm theo người tạo
    List<ExportOrder> findByCreatedBy(User createdBy);

    // Tìm theo ngày tạo trong khoảng
    @Query("SELECT eo FROM ExportOrder eo WHERE eo.createdAt BETWEEN :start AND :end")
    List<ExportOrder> findByCreatedAtBetween(@Param("start") LocalDateTime start,
                                             @Param("end") LocalDateTime end);

    // Tìm tất cả theo nhiều điều kiện
    @Query("""
        SELECT eo FROM ExportOrder eo 
        WHERE (:orderCode IS NULL OR LOWER(eo.orderCode) LIKE LOWER(CONCAT('%', :orderCode, '%')))
        AND (:status IS NULL OR eo.status = :status)
        AND (:createdBy IS NULL OR eo.createdBy = :createdBy)
        AND (:startDate IS NULL OR eo.createdAt >= :startDate)
        AND (:endDate IS NULL OR eo.createdAt <= :endDate)
    """)
    List<ExportOrder> searchOrders(@Param("orderCode") String orderCode,
                                   @Param("status") ExportOrder.Status status,
                                   @Param("createdBy") User createdBy,
                                   @Param("startDate") LocalDateTime startDate,
                                   @Param("endDate") LocalDateTime endDate);

    // lấy các ExportOrder dựa theo SKU
    @Query("SELECT DISTINCT eo FROM ExportOrder eo JOIN ExportOrderDetail eod ON eo.id = eod.exportOrder.id WHERE eod.sku.id = :skuId")
    List<ExportOrder> findExportOrdersBySkuId(@Param("skuId") Integer skuId);

    // Danh sách đơn hàng "confirmed" có từ khóa ưu tiên trong note
    @Query("SELECT eo FROM ExportOrder eo " +
            "WHERE eo.status = com.example.warehousesystem.entity.ExportOrder.Status.confirmed " +
            "AND LOWER(eo.note) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "ORDER BY eo.createdAt ASC")
    List<ExportOrder> findPriorityOrders(@Param("keyword") String keyword);

    // Lấy cả đơn thường và đơn gấp
    @Query("SELECT eo FROM ExportOrder eo " +
            "WHERE eo.status = com.example.warehousesystem.entity.ExportOrder.Status.confirmed " +
            "ORDER BY CASE " +
            "WHEN LOWER(eo.note) LIKE '%gấp%' THEN 0 " +
            "WHEN LOWER(eo.note) LIKE '%ưu tiên%' THEN 1 " +
            "ELSE 2 END, eo.createdAt ASC")
    List<ExportOrder> findAllOrdersWithPriority();
}
