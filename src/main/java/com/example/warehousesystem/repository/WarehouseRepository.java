package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Integer> {

    List<Warehouse> findByNameContainingIgnoreCase(String name);

    List<Warehouse> findByLocationContainingIgnoreCase(String location);
}
