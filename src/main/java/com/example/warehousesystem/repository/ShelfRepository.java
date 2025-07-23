package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.Shelf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShelfRepository extends JpaRepository<Shelf, Integer> {

    // Lấy toàn bộ kệ
    List<Shelf> findAllByOrderByIdAsc();
}
