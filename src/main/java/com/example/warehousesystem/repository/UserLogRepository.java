package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.UserLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface UserLogRepository extends JpaRepository<UserLog, Integer> {

    // Lọc theo hành động của người dùng nhất định
    @Query("SELECT ul FROM UserLog ul WHERE ul.user.id = :userId AND ul.action = :action")
    List<UserLog> findByUserIdAndAction(@Param("userId") Integer userId, @Param("action") String action);

    // Tìm theo bảng bị tác động (target_table)
    List<UserLog> findByTargetTable(String table);

    // Lấy toàn bộ log của thao tác cụ thể
    List<UserLog> findByAction(String action);

    //  Tìm theo user_id
    @Query("SELECT ul FROM UserLog ul WHERE ul.user.id = :userId")
    List<UserLog> findByUserId(@Param("userId") Integer userId);

    // Tìm theo khoảng thời gian
    @Query("SELECT ul FROM UserLog ul WHERE ul.timestamp BETWEEN :start AND :end")
    List<UserLog> findByTimestampBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // Tìm theo hành động
    List<UserLog> findByActionContainingIgnoreCase(String action);

    // Tìm theo IP
    List<UserLog> findByIpAddress(String ipAddress);
}
