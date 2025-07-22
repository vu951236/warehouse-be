package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.ExportOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExportOrderRepository extends JpaRepository<ExportOrder, Integer> {

    Optional<ExportOrder> findByOrderCode(String orderCode);

    List<ExportOrder> findByStatus(ExportOrder.Status status);

    List<ExportOrder> findByCreatedBy_Id(Integer userId);
}
