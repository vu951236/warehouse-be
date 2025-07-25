package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.ReorganizeHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReorganizeHistoryRepository extends JpaRepository<ReorganizeHistory, Integer> {

}
