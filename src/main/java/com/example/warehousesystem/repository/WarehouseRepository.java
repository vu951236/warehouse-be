package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WarehouseRepository extends JpaRepository<Warehouse, Integer> {
    //Tình trạng sức chứa
    @Query(value = """
    SELECT 
        w.name AS warehouse_name,
        COALESCE(SUM(b.used_capacity), 0) AS used_capacity,
        COUNT(DISTINCT s.id) AS shelf_count,
        COALESCE(SUM(s.bin_count), 0) AS total_bin_count,
        COALESCE(MAX(b1.capacity), 0) AS bin_capacity
    FROM warehouse w
    LEFT JOIN shelf s ON s.warehouse_id = w.id
    LEFT JOIN bin b1 ON b1.shelf_id = s.id
    LEFT JOIN box b ON b.bin_id = b1.id
    WHERE w.id = :warehouseId
    GROUP BY w.name
""", nativeQuery = true)
    Object[] getWarehouseStorageStatusById(@Param("warehouseId") Integer warehouseId);

}