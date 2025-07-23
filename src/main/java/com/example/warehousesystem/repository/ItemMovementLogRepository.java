package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.ItemMovementLog;
import com.example.warehousesystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ItemMovementLogRepository extends JpaRepository<ItemMovementLog, Integer> {
    List<ItemMovementLog> findByItemId(Integer itemId);

    List<ItemMovementLog> findByAction(ItemMovementLog.Action action);

    List<ItemMovementLog> findByCreatedById(Integer userId);

    List<ItemMovementLog> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // Tìm theo mã liên kết đơn nhập
    List<ItemMovementLog> findByRelatedOrder(String relatedOrder);

    // Lấy log nhập kho theo khoảng thời gian và hành động
    List<ItemMovementLog> findByActionAndCreatedAtBetween(ItemMovementLog.Action action,
                                                          LocalDateTime start,
                                                          LocalDateTime end);

    // Lấy log theo người tạo
    List<ItemMovementLog> findByCreatedBy(User user);
}
