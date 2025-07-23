package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.Item;
import com.example.warehousesystem.entity.SKU;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {

    // Tổng tồn kho hiện tại theo SKU (trạng thái available hoặc returned)
    @Query("SELECT i.sku.id, COUNT(i.id) " +
            "FROM Item i " +
            "WHERE i.status IN (com.example.warehousesystem.entity.Item.Status.available, " +
            "                   com.example.warehousesystem.entity.Item.Status.returned) " +
            "GROUP BY i.sku.id")
    List<Object[]> totalStockBySKU();

    // Số lượng các loại hàng
    @Query("SELECT i.sku.type AS type, COUNT(i.id) AS count " +
            "FROM Item i GROUP BY i.sku.type")
    List<SKURepository.TypeCount> countItemBySkuType();

    boolean existsByBarcode(String barcode); // đảm bảo không trùng mã vạch

    // Tìm theo mã vạch và Sku_Id
    Item findByBarcode(String barcode);

    List<Item> findBySku_Id(Integer skuId);

    List<Item> findBySkuAndStatus(SKU sku, Item.Status status);

    List<Item> findByStatus(Item.Status status);

    List<Item> findBySku(SKU sku);

    // Đếm số lượng item trong một bin cụ thể
    @Query("SELECT COUNT(i) FROM Item i WHERE i.box.bin.id = :binId AND i.status = 'available'")
    Long countAvailableItemsInBin(@Param("binId") Integer binId);

    // Lấy tất cả item trong bin
    @Query("SELECT i FROM Item i WHERE i.box.bin.id = :binId")
    List<Item> findItemsByBinId(@Param("binId") Integer binId);

    @Query("SELECT i FROM Item i WHERE i.createdAt BETWEEN :start AND :end AND i.status = 'available'")
    List<Item> findImportedItemsBetween(LocalDateTime start, LocalDateTime end);
}
