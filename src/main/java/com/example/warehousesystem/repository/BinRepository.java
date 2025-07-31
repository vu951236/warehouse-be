package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.Bin;
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
        JOIN Box bx ON bx.bin = b
        JOIN SKU sku ON bx.sku = sku
        WHERE (:shelfId IS NULL OR s.id = :shelfId)
          AND (:binId IS NULL OR b.id = :binId)
          AND (:boxId IS NULL OR bx.id = :boxId)
          AND (:skuId IS NULL OR sku.id = :skuId)
    """)
    List<Bin> searchBins(
            @Param("shelfId") Integer shelfId,
            @Param("binId") Integer binId,
            @Param("boxId") Integer boxId,
            @Param("skuId") Integer skuId
    );

    //Thêm kệ hàng
    boolean existsByBinCode(String binCode);

    @Query("""
    SELECT b FROM Bin b
    JOIN FETCH b.shelf
    WHERE b.id = :id
""")
    Optional<Bin> findWithShelfById(@Param("id") Integer id);

    //Xóa shelf
    List<Bin> findByShelfIdAndIsDeletedFalse(Integer shelfId);

}
