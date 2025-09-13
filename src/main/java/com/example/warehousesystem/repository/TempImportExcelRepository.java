package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.TempImportExcel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TempImportExcelRepository extends JpaRepository<TempImportExcel, Long> {
    List<TempImportExcel> findByUserId(Long userId);
}
