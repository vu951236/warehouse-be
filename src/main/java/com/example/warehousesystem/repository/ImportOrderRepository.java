package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.ImportOrder;
import com.example.warehousesystem.entity.ImportOrder.Source;
import com.example.warehousesystem.entity.ImportOrder.Status;
import com.example.warehousesystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImportOrderRepository extends JpaRepository<ImportOrder, Integer> {

    List<ImportOrder> findByCreatedBy(User user);

    List<ImportOrder> findByStatus(Status status);

    List<ImportOrder> findBySource(Source source);

    List<ImportOrder> findByStatusAndCreatedBy(Status status, User user);
}
