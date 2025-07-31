package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.Bin;
import com.example.warehousesystem.entity.Item;
import com.example.warehousesystem.entity.SKU;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Integer> {

    //Nhập kho item
    @Query("SELECT COUNT(i) > 0 FROM Item i WHERE i.barcode = :barcode")
    boolean existsByBarcode(@Param("barcode") String barcode);

    //Xuất kho item
    @Query("SELECT COUNT(i) > 0 FROM Item i WHERE i.barcode = :barcode AND i.status = 'available'")
    boolean isBarcodeAvailableForExport(@Param("barcode") String barcode);

    //Xuất kho item
    @Modifying
    @Query("UPDATE Item i SET i.status = 'exported' WHERE i.barcode = :barcode")
    void markItemAsExported(@Param("barcode") String barcode);

    //[Thuật toán] Quét mã xếp item về đơn hàng sau khi lấy hàng khỏi kệ
    Optional<Item> findByBarcode(String barcode);

    //Tìm kiếm item
    @Query("""
        SELECT i FROM Item i
        JOIN i.box b
        JOIN i.sku s
        LEFT JOIN ExportOrderDetail eod ON eod.sku.id = s.id
        LEFT JOIN ImportOrderDetail iod ON iod.sku.id = s.id
        WHERE (:itemId IS NULL OR i.id = :itemId)
        AND (:boxId IS NULL OR b.id = :boxId)
        AND (:skuId IS NULL OR s.id = :skuId)
        AND (:barcode IS NULL OR i.barcode LIKE %:barcode%)
        AND (:exportOrderId IS NULL OR eod.exportOrder.id = :exportOrderId)
        AND (:importOrderId IS NULL OR iod.importOrder.id = :importOrderId)
    """)
    List<Item> searchItems(Integer boxId, Integer skuId, String barcode, Integer exportOrderId, Integer importOrderId);

    //Xóa shelf,bin,box
    List<Item> findByBoxIdInAndIsDeletedFalse(List<Integer> boxIds);
}
