package com.example.warehousesystem.repository;

import com.example.warehousesystem.dto.response.WarehouseStorageStatusProjection;
import com.example.warehousesystem.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.Optional;

public interface WarehouseRepository extends JpaRepository<Warehouse, Integer> {
    //Tình trạng sức chứa
    @Query(value = """
    SELECT 
        w.name AS warehouseName,
        COALESCE(SUM(b.used_capacity), 0) AS usedCapacity,
        COUNT(DISTINCT s.id) AS shelfCount,
        COALESCE(SUM(s.bin_count), 0) AS totalBinCount,
        COALESCE(MAX(b1.capacity), 0) AS binCapacity
    FROM warehouse w
    LEFT JOIN shelf s ON s.warehouse_id = w.id
    LEFT JOIN bin b1 ON b1.shelf_id = s.id
    LEFT JOIN box b ON b.bin_id = b1.id
    WHERE w.id = :warehouseId
    GROUP BY w.name
""", nativeQuery = true)
    WarehouseStorageStatusProjection getWarehouseStorageStatusById(@Param("warehouseId") Integer warehouseId);

    Optional<Warehouse> findByName(String name);
}