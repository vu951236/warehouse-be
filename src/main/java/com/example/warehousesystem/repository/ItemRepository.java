package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.Item;
import com.example.warehousesystem.entity.Box;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    //Nhập kho item
    boolean existsByBarcode(String barcode);

    //Xuất kho item
    @Query("""
                SELECT i FROM Item i
                JOIN FETCH i.sku
                JOIN FETCH i.box b
                WHERE i.barcode IN :barcodes AND i.isDeleted = false
            """)
    List<Item> findItemsByBarcodes(@Param("barcodes") List<String> barcodes);

    //Tìm kiếm item
    @Query("""
                SELECT i FROM Item i
                JOIN i.box b
                JOIN i.sku s
                LEFT JOIN ExportOrderDetail eod ON eod.sku.id = i.sku.id
                LEFT JOIN ImportOrderDetail iod ON iod.sku.id = i.sku.id
                LEFT JOIN ExportOrder eo ON eo.id = eod.exportOrder.id
                LEFT JOIN ImportOrder io ON io.id = iod.importOrder.id
                WHERE i.isDeleted = false
                AND (:barcode IS NULL OR i.barcode LIKE %:barcode%)
                AND (:boxCode IS NULL OR b.boxCode LIKE %:boxCode%)
                AND (:skuCode IS NULL OR s.skuCode LIKE %:skuCode%)
                AND (:status IS NULL OR i.status = :status)
                AND (:exportCode IS NULL OR eo.exportCode LIKE %:exportCode%)
                AND (:importCode IS NULL OR io.importCode LIKE %:importCode%)
            """)
    List<Item> searchItems(
            @Param("barcode") String barcode,
            @Param("boxCode") String boxCode,
            @Param("skuCode") String skuCode,
            @Param("status") Item.Status status,
            @Param("exportCode") String exportCode,
            @Param("importCode") String importCode
    );

    //Xóa shelf,bin,box
    List<Item> findByBoxIdInAndIsDeletedFalse(List<Integer> boxIds);

    @Query("SELECT i FROM Item i WHERE i.barcode = :barcode")
    Optional<Item> findByBarcode(@Param("barcode") String barcode);

    int countBySkuId(Integer id);

    @Query("SELECT i.box.id, COUNT(i) FROM Item i WHERE i.isDeleted = false GROUP BY i.box.id")
    List<Object[]> countItemAvailableInAllBoxes();

    List<Item> findAllByIsDeletedFalse();

    @Query("""
        SELECT i FROM Item i
        WHERE i.sku.id = :skuId
        AND i.status = :status
        AND i.isDeleted = false
        """)
    List<Item> findAvailableItemsBySku(@Param("skuId") Integer skuId,
                                       @Param("status") Item.Status status,
                                       org.springframework.data.domain.Pageable pageable);

    default List<Item> findAvailableItemsBySku(Integer skuId, int quantity) {
        return findAvailableItemsBySku(
                skuId,
                Item.Status.available,
                org.springframework.data.domain.PageRequest.of(0, quantity)
        );
    }



}
