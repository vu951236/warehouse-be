package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.Item;
import com.example.warehousesystem.entity.SKU;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface SKURepository extends JpaRepository<SKU, Integer> {

    @Query("SELECT i.sku.type AS type, COUNT(i.id) AS count " +
            "FROM Item i " +
            "GROUP BY i.sku.type")
    List<TypeCount> countItemsByType();

    //lọc theo trạng thái
    @Query("SELECT i.sku.type AS type, COUNT(i.id) AS count " +
            "FROM Item i " +
            "WHERE i.status = :status " +
            "GROUP BY i.sku.type")
    List<TypeCount> countItemsByTypeAndStatus(@Param("status") Item.Status status);

    // Interface dùng để ánh xạ kết quả
    public interface TypeCount {
        String getType();
        Long getCount();
    }

    // Tìm theo mã SKU
    SKU findBySkuCode(String skuCode);

    // Tìm theo tên gần đúng
    List<SKU> findByNameContainingIgnoreCase(String keyword);

    boolean existsBySkuCode(String skuCode);

    // Tìm kiếm nâng cao
    @Query("""
        SELECT s FROM SKU s 
        WHERE (:name IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%')))
          AND (:type IS NULL OR s.type = :type)
    """)
    List<SKU> searchSKU(String name, String type);

    // Lấy unit volume của một SKU
    @Query("SELECT s.unitVolume FROM SKU s WHERE s.id = :skuId")
    Float findUnitVolumeById(@Param("skuId") Integer skuId);
}
