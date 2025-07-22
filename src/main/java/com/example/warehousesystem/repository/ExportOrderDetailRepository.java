package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.ExportOrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExportOrderDetailRepository extends JpaRepository<ExportOrderDetail, Integer> {

    List<ExportOrderDetail> findByExportOrder_Id(Integer exportOrderId);

    ExportOrderDetail findByExportOrder_IdAndSku_Id(Integer exportOrderId, Integer skuId);
}
