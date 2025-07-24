package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.ExportOrder;
import com.example.warehousesystem.entity.ExportOrderDetail;
import com.example.warehousesystem.entity.SKU;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

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

    // Lấy danh sách chi tiết đơn xuất theo đơn hàng
    List<ExportOrderDetail> findByExportOrder(ExportOrder exportOrder);

    // Tìm theo SKU
    List<ExportOrderDetail> findBySku(SKU sku);

    // lọc theo SKUid
    @Query("SELECT eod FROM ExportOrderDetail eod WHERE eod.sku.id = :skuId")
    List<ExportOrderDetail> findBySkuId(@Param("skuId") Integer skuId);

    //Lấy danh sách chi tiết đơn xuất theo ID đơn hàng
    List<ExportOrderDetail> findByExportOrderId(Integer exportOrderId);

    //Tìm chi tiết đơn xuất theo ID đơn hàng và SKU
    @Query("SELECT e FROM ExportOrderDetail e WHERE e.exportOrder.id = :orderId AND e.sku.id = :skuId")
    Optional<ExportOrderDetail> findByOrderIdAndSkuId(@Param("orderId") Integer orderId, @Param("skuId") Integer skuId);

}
