package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.Box;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoxRepository extends JpaRepository<Box, Integer> {

    List<Box> findByBin_Id(Integer binId);

    Optional<Box> findByBin_IdAndSku_Id(Integer binId, Integer skuId);
}
