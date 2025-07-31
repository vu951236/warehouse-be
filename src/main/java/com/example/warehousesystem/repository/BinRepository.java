package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.Bin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BinRepository extends JpaRepository<Bin, Integer> {
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
