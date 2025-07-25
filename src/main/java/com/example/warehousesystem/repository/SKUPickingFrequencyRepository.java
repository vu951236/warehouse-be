package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.SKUPickingFrequency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SKUPickingFrequencyRepository extends JpaRepository<SKUPickingFrequency, Integer> {

}
