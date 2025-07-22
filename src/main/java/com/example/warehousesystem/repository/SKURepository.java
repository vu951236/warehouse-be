package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.SKU;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface SKURepository extends JpaRepository<SKU, Integer> {

    Optional<SKU> findBySkuCode(String skuCode);

    List<SKU> findByNameContainingIgnoreCase(String name);

    boolean existsBySkuCode(String skuCode);
}
