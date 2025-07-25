package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.Bin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BinRepository extends JpaRepository<Bin, Integer> {

}
