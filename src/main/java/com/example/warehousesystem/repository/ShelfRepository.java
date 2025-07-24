package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.Shelf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShelfRepository extends JpaRepository<Shelf, Integer> {

    // Lấy toàn bộ kệ
    List<Shelf> findAllByOrderByIdAsc();

    //danh sách các shelf theo ID, kèm theo thông tin warehouse
    @Query("SELECT s FROM Shelf s JOIN FETCH s.warehouse WHERE s.id IN :shelfIds")
    List<Shelf> findShelvesWithWarehouse(@Param("shelfIds") List<Integer> shelfIds);

    //Tìm shelf với warehouseid
    List<Shelf> findByWarehouseId(Integer warehouseId);

    // Tìm kệ theo mã
    List<Shelf> findByShelfCodeContainingIgnoreCase(String shelfCode);

    boolean existsByShelfCode(String shelfCode); // Để kiểm tra trùng mã
}
