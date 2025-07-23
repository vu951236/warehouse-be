package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.ImportOrder;
import com.example.warehousesystem.entity.ImportOrderDetail;
import com.example.warehousesystem.entity.SKU;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ImportOrderDetailRepository extends JpaRepository<ImportOrderDetail, Integer> {

    // Tổng số lượng đã nhập theo ngày
    @Query("SELECT DATE(d.importOrder.createdAt), SUM(d.receivedQuantity) " +
            "FROM ImportOrderDetail d " +
            "GROUP BY DATE(d.importOrder.createdAt) " +
            "ORDER BY DATE(d.importOrder.createdAt)")
    List<Object[]> totalReceivedQuantityByDate();

    // Tổng số lượng đã nhập theo SKU và ngày (phục vụ biểu đồ stacked bar)
    @Query("SELECT d.sku.id, DATE(d.importOrder.createdAt), SUM(d.receivedQuantity) " +
            "FROM ImportOrderDetail d " +
            "GROUP BY d.sku.id, DATE(d.importOrder.createdAt)")
    List<Object[]> totalReceivedQuantityBySKUAndDate();

    // Tổng số lượng hàng đã nhận theo SKU
    @Query("SELECT d.sku.id, SUM(d.receivedQuantity) " +
            "FROM ImportOrderDetail d " +
            "WHERE d.importOrder.status = com.example.warehousesystem.entity.ImportOrder.Status.confirmed " +
            "GROUP BY d.sku.id")
    List<Object[]> totalReceivedQuantityBySKU();

    // Lấy tổng số lượng nhập theo từng loại hàng (type)
    @Query("SELECT d.sku.type, SUM(d.receivedQuantity) FROM ImportOrderDetail d GROUP BY d.sku.type")
    List<Object[]> getTotalImportQuantityByType();

    // Tìm chi tiết đơn theo đơn nhập
    List<ImportOrderDetail> findByImportOrder(ImportOrder importOrder);

    // Tìm theo SKU
    List<ImportOrderDetail> findBySku(SKU sku);

    // Lấy toàn bộ đơn nhập theo SKU ID
    List<ImportOrderDetail> findBySkuId(Integer skuId);

    // Tìm chi tiết đơn theo cả đơn nhập và SKU
    List<ImportOrderDetail> findByImportOrderAndSku(ImportOrder importOrder, SKU sku);

    // Tìm kiếm chi tiết theo mã SKU
    @Query("""
        SELECT iod FROM ImportOrderDetail iod 
        WHERE (:skuCode IS NULL OR iod.sku.skuCode LIKE %:skuCode%)
    """)
    List<ImportOrderDetail> searchBySkuCode(@Param("skuCode") String skuCode);

    // Tìm chi tiết theo id phiếu nhập
    List<ImportOrderDetail> findByImportOrderId(Integer importOrderId);

    // Xoá tất cả chi tiết của phiếu nhập (nếu chỉnh sửa toàn bộ)
    void deleteByImportOrderId(Integer importOrderId);
}

