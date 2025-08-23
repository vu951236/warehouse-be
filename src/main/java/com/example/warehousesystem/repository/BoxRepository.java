package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.Box;
import com.example.warehousesystem.entity.SKU;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
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
    List<Box> findAvailableBoxes(@Param("skuId") Integer skuId, @Param("requiredVolume") Float requiredVolume);

    @Query("SELECT COUNT(b) FROM Box b WHERE b.bin.id = :binId")
    int countBoxesInBin(@Param("binId") Integer binId);

    @Query("SELECT b.bin.id, COUNT(b) FROM Box b WHERE b.isDeleted = false GROUP BY b.bin.id")
    List<Object[]> countBoxesAvailableInAllBins();

    //[Thuật toán] Đường đi lấy hàng tối ưu
    @Query("""
    SELECT b FROM Box b
    JOIN FETCH b.bin bin
    JOIN FETCH bin.shelf shelf
    JOIN FETCH shelf.warehouse
    WHERE b.isDeleted = false 
    AND b.sku.id IN :skuIds 
    AND b.usedCapacity > 0
    ORDER BY shelf.id ASC, bin.id ASC, b.id ASC
""")
    List<Box> findAvailableBoxesBySkuIds(@Param("skuIds") List<Integer> skuIds);

    //Tìm kiếm Box
    @Query("""
        SELECT DISTINCT bx FROM Box bx
        JOIN FETCH bx.bin b
        JOIN FETCH bx.sku s
        LEFT JOIN Item i ON i.box = bx
        WHERE bx.isDeleted = false\s
        AND (:binCode IS NULL OR b.binCode LIKE %:binCode%)
        AND (:boxCode IS NULL OR bx.boxCode LIKE %:boxCode%)
        AND (:skuCode IS NULL OR s.skuCode LIKE %:skuCode%)
        AND (:barcode IS NULL OR i.barcode LIKE %:barcode%)
    """)
    List<Box> searchBoxes(
          @Param("binCode") String binCode,
          @Param("boxCode") String boxCode,
          @Param("skuCode") String skuCode,
          @Param("barcode") String barcode
    );

    @Query("""
        SELECT b FROM Box b
        JOIN FETCH b.bin bi
        JOIN FETCH b.sku s
        WHERE b.isDeleted = false 
        AND b.id = :id
    """)
    Optional<Box> findWithBinAndSkuById(Integer id);

    //Xóa shelf,bin
    List<Box> findByBinIdInAndIsDeletedFalse(List<Integer> binIds);

    //Thêm box
    boolean existsByBoxCode(String boxCode);

    Optional<Box> findByBoxCode(String boxCode);

    List<Box> findAllByIsDeletedFalse();

    int countByBinId(Integer binId);

    Optional<Box> findByIdAndIsDeletedFalse(Integer id);

    List<Box> findBySkuAndIsDeletedFalse(SKU sku);

    @Query("SELECT COUNT(i) FROM Item i WHERE i.box.id = :boxId")
    Long countItemsInBox(@Param("boxId") Integer boxId);
}
