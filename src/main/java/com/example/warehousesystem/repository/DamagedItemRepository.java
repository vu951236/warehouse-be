package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.DamagedItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DamagedItemRepository extends JpaRepository<DamagedItem, Integer> {
    Optional<DamagedItem> findByBarcode(String barcode);
}
