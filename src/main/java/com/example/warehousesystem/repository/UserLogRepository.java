package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.UserLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface UserLogRepository extends JpaRepository<UserLog, Integer> {

    @Query("""
    SELECT ul
    FROM UserLog ul
    JOIN ul.user u
    WHERE (:warehouseId IS NULL OR ul.targetId = :warehouseId)
      AND ul.timestamp BETWEEN :start AND :end
    ORDER BY ul.timestamp DESC
""")
    List<UserLog> findLogsByWarehouseAndDateRange(
            @Param("warehouseId") Integer warehouseId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );


    @Query("""
        SELECT ul
        FROM UserLog ul
        WHERE (:warehouseId IS NULL OR ul.targetId = :warehouseId)
        ORDER BY ul.timestamp DESC
    """)
    List<UserLog> findByWarehouse(@Param("warehouseId") Integer warehouseId);
}
