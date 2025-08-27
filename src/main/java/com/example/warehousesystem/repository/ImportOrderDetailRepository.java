package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.ImportOrder;
import com.example.warehousesystem.entity.ImportOrderDetail;
import com.example.warehousesystem.entity.SKU;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


public interface ImportOrderDetailRepository extends JpaRepository<ImportOrderDetail, Integer> {
    //Xem thông tin nhập theo SKU
    @Query("""
    SELECT iod, io, wh.name
    FROM ImportOrderDetail iod
    JOIN iod.importOrder io
    JOIN iod.sku s
    JOIN Box b ON b.sku.id = s.id
    JOIN Bin bin ON b.bin.id = bin.id
    JOIN Shelf sh ON bin.shelf.id = sh.id
    JOIN Warehouse wh ON sh.warehouse.id = wh.id
    WHERE s.skuCode = :skuCode
      AND (:fromDate IS NULL OR io.createdAt >= :fromDate)
      AND (:toDate IS NULL OR io.createdAt <= :toDate)
    ORDER BY io.createdAt DESC
""")
    List<Object[]> findImportDetailsBySku(
            @Param("skuCode") String skuCode,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );

    //Xem thông tin các lần nhập
    @Query("SELECT d FROM ImportOrderDetail d WHERE d.importOrder.id = :orderId")
    List<ImportOrderDetail> findByImportOrderId(@Param("orderId") Integer orderId);

    @Query("""
    SELECT d FROM ImportOrderDetail d
    JOIN d.importOrder o
    JOIN d.sku s
    WHERE (:importCode IS NULL OR o.importCode = :importCode)
      AND (:skuCode IS NULL OR s.skuCode = :skuCode)
      AND (:startDate IS NULL OR o.createdAt >= :startDate)
      AND (:endDate IS NULL OR o.createdAt <= :endDate)
""")
    List<ImportOrderDetail> searchImportOrdersV2(
            @Param("importCode") String importCode,
            @Param("skuCode") String skuCode,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
    @Query("""
    SELECT d FROM ImportOrderDetail d
    WHERE (:importCode IS NULL OR d.importOrder.importCode = :importCode)
      AND (:source IS NULL OR d.importOrder.source = :source)
      AND (:startDate IS NULL OR d.importOrder.createdAt >= :startDate)
      AND (:endDate IS NULL OR d.importOrder.createdAt <= :endDate)
""")
    List<ImportOrderDetail> searchImportOrdersMerged(
            @Param("importCode") String importCode,
            @Param("source") ImportOrder.Source source,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );


}

