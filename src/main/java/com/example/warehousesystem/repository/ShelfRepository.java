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
    //Tìm kiếm kệ hàng
    @Query("""
    SELECT DISTINCT s FROM Shelf s
    JOIN FETCH s.warehouse w
    JOIN Bin b ON b.shelf = s
    JOIN Box bx ON bx.bin = b
    JOIN SKU sku ON bx.sku = sku
    WHERE (:shelfId IS NULL OR s.id = :shelfId)
      AND (:warehouseId IS NULL OR w.id = :warehouseId)
      AND (:binId IS NULL OR b.id = :binId)
      AND (:boxId IS NULL OR bx.id = :boxId)
      AND (:skuId IS NULL OR sku.id = :skuId)
""")
    List<Shelf> searchShelves(
            @Param("shelfId") Integer shelfId,
            @Param("warehouseId") Integer warehouseId,
            @Param("binId") Integer binId,
            @Param("boxId") Integer boxId,
            @Param("skuId") Integer skuId
    );

    //Thêm kệ hàng
    boolean existsByShelfCode(String shelfCode);

    @Query("SELECT s FROM Shelf s JOIN FETCH s.warehouse WHERE s.id = :id")
    Optional<Shelf> findWithWarehouseById(Integer id);

    //[ Thuật toán] Phân bổ lại vị trí SKU theo độ picking hằng tháng
    @Query("SELECT s FROM Shelf s WHERE s.isDeleted = false ORDER BY s.id ASC")
    List<Shelf> findAvailableShelvesOrderedById();
}
