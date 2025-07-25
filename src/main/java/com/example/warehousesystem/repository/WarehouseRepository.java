package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WarehouseRepository extends JpaRepository<Warehouse, Integer> {
    //Tình trạng sức chứa
    @Query(value = """
    SELECT 
        w.name AS warehouse_name,
        COALESCE(SUM(b.capacity), 0) AS total_capacity,
        COALESCE(SUM(b.used_capacity), 0) AS used_capacity
    FROM warehouse w
    LEFT JOIN shelf s ON s.warehouse_id = w.id
    LEFT JOIN bin b1 ON b1.shelf_id = s.id
    LEFT JOIN box b ON b.bin_id = b1.id
    GROUP BY w.name
""", nativeQuery = true)
    List<Object[]> getWarehouseStorageStatus();
}