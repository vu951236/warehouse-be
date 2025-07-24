package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.ItemMovementLog;
import com.example.warehousesystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ItemMovementLogRepository extends JpaRepository<ItemMovementLog, Integer> {
    List<ItemMovementLog> findByItemId(Integer itemId);

    List<ItemMovementLog> findByAction(ItemMovementLog.Action action);

    List<ItemMovementLog> findByCreatedById(Integer userId);

    List<ItemMovementLog> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // Lấy log nhập kho theo khoảng thời gian và hành động
    List<ItemMovementLog> findByActionAndCreatedAtBetween(ItemMovementLog.Action action,
                                                          LocalDateTime start,
                                                          LocalDateTime end);

    //tìm kiếm lịch sử nhập kho.
    @Query("SELECT l FROM ItemMovementLog l WHERE l.action = 'import' " +
            "AND (:start IS NULL OR l.createdAt >= :start) " +
            "AND (:end IS NULL OR l.createdAt <= :end) " +
            "AND (:relatedOrder IS NULL OR l.relatedOrder LIKE %:relatedOrder%)")
    List<ItemMovementLog> searchImportLogs(@Param("start") LocalDateTime start,
                                           @Param("end") LocalDateTime end,
                                           @Param("relatedOrder") String relatedOrder);

    // Lấy tất cả các log có action là 'import'
    @Query("SELECT l FROM ItemMovementLog l WHERE l.action = 'import'")
    List<ItemMovementLog> findAllImportLogs();

    // Lấy log theo người tạo
    List<ItemMovementLog> findByCreatedBy(User user);

    // log chi tiết mỗi lần xuất của SKU
    @Query("SELECT iml FROM ItemMovementLog iml WHERE iml.action = 'export' AND iml.item.sku.id = :skuId")
    List<ItemMovementLog> findExportLogsBySkuId(@Param("skuId") Integer skuId);

    //Truy xuất lịch sử dịch chuyển của item theo mã đơn liên quan
    @Query("SELECT i FROM ItemMovementLog i WHERE i.relatedOrder = :relatedOrder")
    List<ItemMovementLog> findByRelatedOrder(@Param("relatedOrder") String relatedOrder);

    //tìm kiếm lịch sử xuất kho.
    @Query("SELECT l FROM ItemMovementLog l WHERE l.action = 'export' " +
            "AND (:start IS NULL OR l.createdAt >= :start) " +
            "AND (:end IS NULL OR l.createdAt <= :end) " +
            "AND (:relatedOrder IS NULL OR l.relatedOrder LIKE %:relatedOrder%)")
    List<ItemMovementLog> searchExportLogs(@Param("start") LocalDateTime start,
                                           @Param("end") LocalDateTime end,
                                           @Param("relatedOrder") String relatedOrder);

    // Lấy tất cả các log có action là 'export'
    @Query("SELECT l FROM ItemMovementLog l WHERE l.action = 'export'")
    List<ItemMovementLog> findAllExportLogs();

    @Query("SELECT l FROM ItemMovementLog l WHERE l.action = 'relocate' " +
            "AND l.createdAt BETWEEN :start AND :end")
    List<ItemMovementLog> findRelocations(@Param("start") LocalDateTime start,
                                          @Param("end") LocalDateTime end);

}
