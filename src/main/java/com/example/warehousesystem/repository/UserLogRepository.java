package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.UserLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLogRepository extends JpaRepository<UserLog, Integer> {

}
