package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.Box;
import com.example.warehousesystem.entity.SKU;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoxRepository extends JpaRepository<Box, Integer> {
    //Nhập kho item
    @Query("""
SELECT bx FROM Box bx 
WHERE bx.isDeleted = false 
AND bx.sku.id = :skuId 
AND (bx.capacity - bx.usedCapacity) >= :requiredVolume
""")
    List<Box> findAvailableBoxes(@Param("skuId") Integer skuId, @Param("requiredVolume") Integer requiredVolume);

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

    //Tìm kiếm Box
    @Query("""
        SELECT DISTINCT bx FROM Box bx
        JOIN FETCH bx.bin b
        JOIN FETCH bx.sku s
        LEFT JOIN Item i ON i.box = bx
        WHERE (:binId IS NULL OR b.id = :binId)
          AND (:boxId IS NULL OR bx.id = :boxId)
          AND (:skuId IS NULL OR s.id = :skuId)
          AND (:itemId IS NULL OR i.id = :itemId)
    """)
    List<Box> searchBoxes(
            @Param("binId") Integer binId,
            @Param("boxId") Integer boxId,
            @Param("skuId") Integer skuId,
            @Param("itemId") Integer itemId
    );

    @Query("""
        SELECT b FROM Box b
        JOIN FETCH b.bin bi
        JOIN FETCH b.sku s
        WHERE b.id = :id
    """)
    Optional<Box> findWithBinAndSkuById(Integer id);

    //Xóa shelf,bin
    List<Box> findByBinIdInAndIsDeletedFalse(List<Integer> binIds);
}
