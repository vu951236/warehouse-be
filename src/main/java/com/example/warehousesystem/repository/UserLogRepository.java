package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.User;
import com.example.warehousesystem.entity.UserLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserLogRepository extends JpaRepository<UserLog, Integer> {

    List<UserLog> findByUser(User user);

    List<UserLog> findByUserAndAction(User user, String action);

    List<UserLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    List<UserLog> findByTargetTable(String targetTable);

    List<UserLog> findByTargetId(Integer targetId);
}
