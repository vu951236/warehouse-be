package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.Shelf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShelfRepository extends JpaRepository<Shelf, Integer> {
    // Tìm kiếm kệ hàng theo mã code
    @Query("""
    SELECT DISTINCT s FROM Shelf s
    JOIN FETCH s.warehouse w
    LEFT JOIN Bin b ON b.shelf = s AND b.isDeleted = false
    LEFT JOIN Box bx ON bx.bin = b AND bx.isDeleted = false
    LEFT JOIN SKU sku ON bx.sku = sku
    WHERE s.isDeleted = false 
      AND (:shelfCode IS NULL OR s.shelfCode LIKE %:shelfCode%)
      AND (:warehouseId IS NULL OR w.id = :warehouseId)
      AND (:binCode IS NULL OR b.binCode LIKE %:binCode%)
      AND (:boxCode IS NULL OR bx.boxCode LIKE %:boxCode%)
      AND (:skuCode IS NULL OR sku.skuCode LIKE %:skuCode%)
""")
    List<Shelf> searchShelves(
            @Param("shelfCode") String shelfCode,
            @Param("warehouseId") Integer warehouseId,
            @Param("binCode") String binCode,
            @Param("boxCode") String boxCode,
            @Param("skuCode") String skuCode
    );

    //Thêm kệ hàng
    boolean existsByShelfCode(String shelfCode);

    @Query("SELECT s FROM Shelf s JOIN FETCH s.warehouse WHERE s.isDeleted = false AND s.id = :id")
    Optional<Shelf> findWithWarehouseById(Integer id);

    //[ Thuật toán] Phân bổ lại vị trí SKU theo độ picking hằng tháng
    @Query("SELECT s FROM Shelf s WHERE s.isDeleted = false ORDER BY s.id ASC")
    List<Shelf> findAvailableShelvesOrderedById();

    List<Shelf> findAllByIsDeletedFalse();

    Optional<Shelf> findByShelfCode(String shelfCode);

}
