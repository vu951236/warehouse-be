package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

}
