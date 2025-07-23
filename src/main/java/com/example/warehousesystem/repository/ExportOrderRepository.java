package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.ExportOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
}
