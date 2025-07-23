package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.UserLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserLogRepository extends JpaRepository<UserLog, Integer> {

    // Lấy các log theo người dùng
    List<UserLog> findByUserId(Integer userId);

    // Lọc theo hành động
    List<UserLog> findByUserIdAndAction(Integer userId, String action);
}
