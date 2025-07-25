package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.ItemMovementLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemMovementLogRepository extends JpaRepository<ItemMovementLog, Integer> {



}
