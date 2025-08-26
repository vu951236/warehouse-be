package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.ImportOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ImportOrderRepository extends JpaRepository<ImportOrder, Integer> {

    //Chart thông tin tổng kết+Chỉ số tối ưu hoá nhập – xuất
    @Query(value = """
    SELECT 
        DATE(io.created_at) AS stat_date,
        COUNT(DISTINCT io.id) AS total_import_orders,
        COALESCE(SUM(iod.quantity), 0) AS total_import_items
    FROM importorder io
    LEFT JOIN importorderdetail iod ON io.id = iod.import_order_id
    WHERE (:warehouseId IS NULL OR EXISTS (
        SELECT 1
        FROM box b
        JOIN bin bi ON b.bin_id = bi.id
        JOIN shelf s ON bi.shelf_id = s.id
        JOIN warehouse w ON s.warehouse_id = w.id
        WHERE b.sku_id = iod.sku_id AND w.id = :warehouseId
    ))
    AND io.created_at BETWEEN :startDate AND :endDate
    GROUP BY stat_date
    ORDER BY stat_date
""", nativeQuery = true)
    List<Object[]> getImportStatistics(
            @Param("warehouseId") Integer warehouseId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

    // ================= KPI =================

    // 1. Tổng số đơn nhập confirmed
    @Query(value = """
        SELECT COUNT(DISTINCT io.id)
        FROM importorder io
        JOIN importorderdetail iod ON io.id = iod.import_order_id
        JOIN sku s ON iod.sku_id = s.id
        JOIN box b ON b.sku_id = s.id
        JOIN bin bi ON b.bin_id = bi.id
        JOIN shelf sh ON bi.shelf_id = sh.id
        JOIN warehouse w ON sh.warehouse_id = w.id
        WHERE io.status = 'confirmed'
          AND (:warehouseId IS NULL OR w.id = :warehouseId)
          AND io.created_at BETWEEN :fromDate AND :toDate
    """, nativeQuery = true)
    Long countConfirmedOrders(
            @Param("warehouseId") Integer warehouseId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    // 2. Tổng số item nhập (quantity)
    @Query(value = """
        SELECT COALESCE(SUM(iod.quantity), 0)
        FROM importorder io
        JOIN importorderdetail iod ON io.id = iod.import_order_id
        JOIN sku s ON iod.sku_id = s.id
        JOIN box b ON b.sku_id = s.id
        JOIN bin bi ON b.bin_id = bi.id
        JOIN shelf sh ON bi.shelf_id = sh.id
        JOIN warehouse w ON sh.warehouse_id = w.id
        WHERE io.status = 'confirmed'
          AND (:warehouseId IS NULL OR w.id = :warehouseId)
          AND io.created_at BETWEEN :fromDate AND :toDate
    """, nativeQuery = true)
    Long sumConfirmedItems(
            @Param("warehouseId") Integer warehouseId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    // 3. Tổng số item nhập từ Factory
    @Query(value = """
        SELECT COALESCE(SUM(iod.quantity), 0)
        FROM importorder io
        JOIN importorderdetail iod ON io.id = iod.import_order_id
        JOIN sku s ON iod.sku_id = s.id
        JOIN box b ON b.sku_id = s.id
        JOIN bin bi ON b.bin_id = bi.id
        JOIN shelf sh ON bi.shelf_id = sh.id
        JOIN warehouse w ON sh.warehouse_id = w.id
        WHERE io.status = 'confirmed'
          AND io.source = 'factory'
          AND (:warehouseId IS NULL OR w.id = :warehouseId)
          AND io.created_at BETWEEN :fromDate AND :toDate
    """, nativeQuery = true)
    Long sumItemsByFactory(
            @Param("warehouseId") Integer warehouseId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            String factory);

    // 4. Tổng số item nhập từ ReturnGoods
    @Query(value = """
        SELECT COALESCE(SUM(iod.quantity), 0)
        FROM importorder io
        JOIN importorderdetail iod ON io.id = iod.import_order_id
        JOIN sku s ON iod.sku_id = s.id
        JOIN box b ON b.sku_id = s.id
        JOIN bin bi ON b.bin_id = bi.id
        JOIN shelf sh ON bi.shelf_id = sh.id
        JOIN warehouse w ON sh.warehouse_id = w.id
        WHERE io.status = 'confirmed'
          AND io.source = 'returnGoods'
          AND (:warehouseId IS NULL OR w.id = :warehouseId)
          AND io.created_at BETWEEN :fromDate AND :toDate
    """, nativeQuery = true)
    Long sumItemsByReturnGoods(
            @Param("warehouseId") Integer warehouseId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            String returnGoods);

    // ================= Chart =================

    @Query(value = """
        SELECT 
            DATE_FORMAT(io.created_at, '%Y-%m-%d') AS import_date,
            COUNT(DISTINCT io.id) AS total_orders,
            COALESCE(SUM(iod.quantity), 0) AS total_items
        FROM importorder io
        JOIN importorderdetail iod ON io.id = iod.import_order_id
        JOIN sku s ON iod.sku_id = s.id
        JOIN box b ON b.sku_id = s.id
        JOIN bin bi ON b.bin_id = bi.id
        JOIN shelf sh ON bi.shelf_id = sh.id
        JOIN warehouse w ON sh.warehouse_id = w.id
        WHERE io.status = 'confirmed'
          AND (:warehouseId IS NULL OR w.id = :warehouseId)
          AND io.created_at BETWEEN :fromDate AND :toDate
        GROUP BY DATE_FORMAT(io.created_at, '%Y-%m-%d')
        ORDER BY DATE_FORMAT(io.created_at, '%Y-%m-%d')
    """, nativeQuery = true)
    List<Object[]> getImportChartData(
            @Param("warehouseId") Integer warehouseId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

}
