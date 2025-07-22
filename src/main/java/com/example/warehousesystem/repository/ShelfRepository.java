package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.Shelf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShelfRepository extends JpaRepository<Shelf, Integer> {

    List<Shelf> findByWarehouseId(Integer warehouseId);

    Shelf findByShelfCode(String shelfCode);
}
