package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.Bin;
import com.example.warehousesystem.entity.Shelf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
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

    List<Bin> findAllByIsDeletedFalse();

    @Query("SELECT bin FROM Bin bin " +
            "JOIN bin.shelf s " +
            "WHERE s.shelfCode IN :shelfCodes " +
            "AND bin.isDeleted = false")
    Optional<Bin> findFirstByShelfCodes(@Param("shelfCodes") List<String> shelfCodes);

    @Query("SELECT b FROM Bin b WHERE b.shelf.shelfCode IN :shelfCodes")
    List<Bin> findBinsWithAvailableCapacityInShelves(@Param("shelfCodes") List<String> shelfCodes);


    // Đếm bin trong warehouse
    @Query("""
        SELECT COUNT(b) 
        FROM Bin b 
        WHERE b.isDeleted = false 
          AND b.shelf.warehouse.id = :warehouseId
    """)
    long countByWarehouseId(@Param("warehouseId") Integer warehouseId);

    // Tổng capacity tất cả bin trong 1 warehouse
    @Query("""
        SELECT COALESCE(SUM(b.capacity),0) 
        FROM Bin b 
        WHERE b.isDeleted = false 
          AND b.shelf.warehouse.id = :warehouseId
    """)
    Double sumCapacityByWarehouseId(@Param("warehouseId") Integer warehouseId);

    // Tổng usedCapacity của box trong tất cả bin
    @Query("""
        SELECT COALESCE(SUM(bx.usedCapacity),0)
        FROM Bin b
        LEFT JOIN Box bx ON bx.bin.id = b.id AND bx.isDeleted = false
        WHERE b.isDeleted = false 
          AND b.shelf.warehouse.id = :warehouseId
    """)
    Double sumUsedCapacityByWarehouseId(@Param("warehouseId") Integer warehouseId);

    // Lấy top 10 bin đầy nhất
    @Query("""
        SELECT b.binCode, b.capacity, COALESCE(SUM(bx.usedCapacity),0)
        FROM Bin b
        LEFT JOIN Box bx ON bx.bin.id = b.id AND bx.isDeleted = false
        WHERE b.isDeleted = false AND b.shelf.warehouse.id = :warehouseId
        GROUP BY b.id, b.binCode, b.capacity
        ORDER BY (COALESCE(SUM(bx.usedCapacity),0) / NULLIF(b.capacity,0)) DESC
    """)
    List<Object[]> findTop10ByUsage(@Param("warehouseId") Integer warehouseId);

}
