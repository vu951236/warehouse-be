package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.ExportOrderDetail;
import com.example.warehousesystem.entity.SKU;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ExportOrderDetailRepository extends JpaRepository<ExportOrderDetail, Integer> {
    //Xem thông tin xuất theo SKU
    @Query("""
    SELECT eod, eo, wh.name
    FROM ExportOrderDetail eod
    JOIN eod.exportOrder eo
    JOIN eod.sku s
    JOIN Box b ON b.sku.id = s.id
    JOIN Bin bin ON b.bin.id = bin.id
    JOIN Shelf sh ON bin.shelf.id = sh.id
    JOIN Warehouse wh ON sh.warehouse.id = wh.id
    WHERE s.skuCode = :skuCode
      AND (:fromDate IS NULL OR eo.createdAt >= :fromDate)
      AND (:toDate IS NULL OR eo.createdAt <= :toDate)
    ORDER BY eo.createdAt DESC
""")
    List<Object[]> findExportDetailsBySku(
            @Param("skuCode") String skuCode,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );

    //Xuất kho item + Xem thông tin các lần xuất
    @Query("SELECT d FROM ExportOrderDetail d WHERE d.exportOrder.id = :orderId")
    List<ExportOrderDetail> findByExportOrderId(@Param("orderId") Integer orderId);

}
