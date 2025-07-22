package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.ImportOrder;
import com.example.warehousesystem.entity.ImportOrderDetail;
import com.example.warehousesystem.entity.SKU;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImportOrderDetailRepository extends JpaRepository<ImportOrderDetail, Integer> {

    List<ImportOrderDetail> findByImportOrder(ImportOrder importOrder);

    List<ImportOrderDetail> findBySku(SKU sku);

    ImportOrderDetail findByImportOrderAndSku(ImportOrder importOrder, SKU sku);
}
