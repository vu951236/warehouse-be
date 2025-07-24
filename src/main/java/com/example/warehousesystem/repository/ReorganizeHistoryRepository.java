package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.ReorganizeHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReorganizeHistoryRepository extends JpaRepository<ReorganizeHistory, Integer> {
    List<ReorganizeHistory> findByExecutedById(Integer userId);

    List<ReorganizeHistory> findByExecutionTimeBetween(LocalDateTime start, LocalDateTime end);

    // Tìm các lần phân bổ lại gần đây
    List<ReorganizeHistory> findByExecutedByIdOrderByExecutionTimeDesc(Integer userId);
}
