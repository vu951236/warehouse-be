package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.Box;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoxRepository extends JpaRepository<Box, Integer> {
    //Tình trạng sức chứa
    @Query("SELECT b FROM Box b " +
            "JOIN FETCH b.bin bn " +
            "JOIN FETCH bn.shelf s " +
            "JOIN FETCH s.warehouse w " +
            "WHERE w.id = :warehouseId")
    List<Box> findByWarehouseId(@Param("warehouseId") Integer warehouseId);

    //Nhập kho item
    @Query("""
    SELECT b FROM Box b
    WHERE b.id = :boxId AND (b.capacity - b.usedCapacity) >= :required
""")
    Optional<Box> findAvailableBox(@Param("boxId") Integer boxId, @Param("required") Integer required);

    //[Thuật toán] Phân bổ hàng vào kho tối ưu
    @Query("""
    SELECT b FROM Box b
    JOIN FETCH b.bin bin
    JOIN FETCH b.sku s
    WHERE s.skuCode = :skuCode
    AND b.capacity > b.usedCapacity
    ORDER BY (b.capacity - b.usedCapacity) DESC
""")
    List<Box> findBoxesBySkuAndRemainingCapacity(@Param("skuCode") String skuCode);

    //[Thuật toán] Phân bổ hàng vào kho tối ưu
    @Query("""
    SELECT b FROM Box b
    JOIN FETCH b.bin bin
    WHERE b.sku IS NULL
    AND b.capacity > b.usedCapacity
    ORDER BY (b.capacity - b.usedCapacity) DESC
""")
    List<Box> findEmptyBoxes();

    //[Thuật toán] Đường đi lấy hàng tối ưu
    @Query("""
    SELECT b FROM Box b
    JOIN FETCH b.bin bin
    JOIN FETCH bin.shelf shelf
    JOIN FETCH shelf.warehouse
    WHERE b.sku.id IN :skuIds AND b.usedCapacity > 0
""")
    List<Box> findAvailableBoxesBySkuIds(@Param("skuIds") List<Integer> skuIds);



}
