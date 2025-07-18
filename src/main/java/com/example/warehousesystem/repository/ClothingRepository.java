package com.example.warehousesystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.warehousesystem.entity.Clothing;

@Repository
public interface ClothingRepository extends JpaRepository<Clothing, Long> {
}
