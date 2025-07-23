package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.ImportOrder;
import com.example.warehousesystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ImportOrderRepository extends JpaRepository<ImportOrder, Integer> {

    // Đếm số đơn nhập theo ngày
    @Query("SELECT DATE(io.createdAt), COUNT(io.id) " +
            "FROM ImportOrder io " +
            "GROUP BY DATE(io.createdAt) " +
            "ORDER BY DATE(io.createdAt)")
    List<Object[]> countImportOrdersByDate();

    // Thống kê số đơn theo trạng thái
    @Query("SELECT io.status, COUNT(io.id) " +
            "FROM ImportOrder io " +
            "GROUP BY io.status")
    List<Object[]> countByStatus();

    // Thống kê số đơn theo nguồn nhập
    @Query("SELECT io.source, COUNT(io.id) " +
            "FROM ImportOrder io " +
            "GROUP BY io.source")
    List<Object[]> countBySource();

    // Đếm số đơn nhập trong khoảng thời gian
    long countByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Tìm kiếm nâng cao theo nhiều tiêu chí
    @Query("""
        SELECT io FROM ImportOrder io
        WHERE (:source IS NULL OR io.source = :source)
          AND (:status IS NULL OR io.status = :status)
          AND (:createdBy IS NULL OR io.createdBy = :createdBy)
          AND (:fromDate IS NULL OR io.createdAt >= :fromDate)
          AND (:toDate IS NULL OR io.createdAt <= :toDate)
    """)
    List<ImportOrder> searchImportOrders(
            @Param("source") ImportOrder.Source source,
            @Param("status") ImportOrder.Status status,
            @Param("createdBy") User createdBy,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );

    // Tìm theo trạng thái và người tạo
    List<ImportOrder> findByStatus(ImportOrder.Status status);
    List<ImportOrder> findByCreatedBy_Id(Integer userId);
    List<ImportOrder> findByStatusAndCreatedBy_Id(ImportOrder.Status status, Integer userId); // kết hợp

    //  Tìm theo khoảng thời gian tạo
    List<ImportOrder> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    //  Tìm theo nguồn nhập
    List<ImportOrder> findBySource(ImportOrder.Source source);

    //  Tìm theo người tạo
    List<ImportOrder> findByCreatedBy(User user);

    // Tất cả các phiếu nhập, mới nhất trước
    List<ImportOrder> findAllByOrderByCreatedAtDesc();
}
