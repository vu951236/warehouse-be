package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.Bin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BinRepository extends JpaRepository<Bin, Integer> {

    List<Bin> findByShelf_Id(Integer shelfId);

    Optional<Bin> findByBinCode(String binCode);
}
