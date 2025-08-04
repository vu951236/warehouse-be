package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.Bin;
import com.example.warehousesystem.entity.Shelf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BinRepository extends JpaRepository<Bin, Integer> {
    //Nhập kho item
    @Query("""
    SELECT b FROM Bin b 
    WHERE b.isDeleted = false 
    AND b.capacity > (
        SELECT COALESCE(SUM(bx.usedCapacity), 0) 
        FROM Box bx 
        WHERE bx.bin = b AND bx.isDeleted = false
    )
    """)
        List<Bin> findBinsWithAvailableCapacity();
    //Nhập kho item
    @Query("""
    SELECT COALESCE(SUM(bx.usedCapacity), 0) FROM Box bx 
    WHERE bx.bin.id = :binId AND bx.isDeleted = false
    """)
        Integer getUsedCapacityInBin(@Param("binId") Integer binId);

    //Tìm kiếm bin
    @Query("""
    SELECT DISTINCT b FROM Bin b
    JOIN FETCH b.shelf s
    LEFT JOIN Box bx ON bx.bin = b AND bx.isDeleted = false
    LEFT JOIN SKU sku ON bx.sku = sku
    WHERE b.isDeleted = false 
      AND (:binCode IS NULL OR b.binCode LIKE %:binCode%)
      AND (:shelfCode IS NULL OR s.shelfCode LIKE %:shelfCode%)
      AND (:boxCode IS NULL OR bx.boxCode LIKE %:boxCode%)
      AND (:skuCode IS NULL OR sku.skuCode LIKE %:skuCode%)
""")
    List<Bin> searchBins(
            @Param("binCode") String binCode,
            @Param("shelfCode") String shelfCode,
            @Param("boxCode") String boxCode,
            @Param("skuCode") String skuCode
    );


    //Thêm kệ hàng
    boolean existsByBinCode(String binCode);

    @Query("""
    SELECT b FROM Bin b
    JOIN FETCH b.shelf
    WHERE b.isDeleted = false 
    AND b.id = :id
""")
    Optional<Bin> findWithShelfById(@Param("id") Integer id);

    //Xóa shelf
    List<Bin> findByShelfIdAndIsDeletedFalse(Integer shelfId);

    Optional<Bin> findByBinCode(String binCode);

}
