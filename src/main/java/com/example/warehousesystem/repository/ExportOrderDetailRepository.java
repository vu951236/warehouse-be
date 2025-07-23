package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.ExportOrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ExportOrderDetailRepository extends JpaRepository<ExportOrderDetail, Integer> {

    // Tổng số lượng xuất theo ngày
    @Query("SELECT DATE(eod.exportOrder.createdAt), SUM(eod.quantity) " +
            "FROM ExportOrderDetail eod " +
            "GROUP BY DATE(eod.exportOrder.createdAt) " +
            "ORDER BY DATE(eod.exportOrder.createdAt)")
    List<Object[]> totalExportQuantityByDate();

    // Tổng số lượng xuất theo SKU và ngày (phục vụ stacked bar chart)
    @Query("SELECT eod.sku.id, DATE(eod.exportOrder.createdAt), SUM(eod.quantity) " +
            "FROM ExportOrderDetail eod " +
            "GROUP BY eod.sku.id, DATE(eod.exportOrder.createdAt)")
    List<Object[]> totalExportQuantityBySKUAndDate();

    // Tổng số lượng xuất theo SKU
    @Query("SELECT d.sku.id, SUM(d.quantity) " +
            "FROM ExportOrderDetail d " +
            "WHERE d.exportOrder.status = com.example.warehousesystem.entity.ExportOrder.Status.confirmed " +
            "GROUP BY d.sku.id")
    List<Object[]> totalExportedQuantityBySKU();

    // Lấy tổng số lượng xuất theo từng loại hàng (type)
    @Query("SELECT d.sku.type, SUM(d.quantity) FROM ExportOrderDetail d GROUP BY d.sku.type")
    List<Object[]> getTotalExportQuantityByType();
}
