package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.Item;
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
    LEFT JOIN ExportOrderDetail eod ON eod.sku.id = s.id
    LEFT JOIN ImportOrderDetail iod ON iod.sku.id = s.id
    WHERE i.isDeleted = false
    AND (:itemId IS NULL OR i.id = :itemId)
    AND (:boxId IS NULL OR b.id = :boxId)
    AND (:skuId IS NULL OR s.id = :skuId)
    AND (:barcode IS NULL OR i.barcode LIKE %:barcode%)
    AND (:status IS NULL OR i.status = :status)
    AND (:exportOrderId IS NULL OR eod.exportOrder.id = :exportOrderId)
    AND (:importOrderId IS NULL OR iod.importOrder.id = :importOrderId)
""")
    List<Item> searchItems(
            Integer itemId,
            Integer boxId,
            Integer skuId,
            String barcode,
            Item.Status status,
            Integer exportOrderId,
            Integer importOrderId
    );

    //Xóa shelf,bin,box
    List<Item> findByBoxIdInAndIsDeletedFalse(List<Integer> boxIds);

    @Query("SELECT i FROM Item i WHERE i.barcode = :barcode")
    Optional<Item> findByBarcode(@Param("barcode") String barcode);

    int countBySkuId(Integer id);
}
