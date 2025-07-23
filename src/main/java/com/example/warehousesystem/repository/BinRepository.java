package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.Bin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BinRepository extends JpaRepository<Bin, Integer> {

    // Tìm danh sách bin có đủ sức chứa
    @Query("SELECT b FROM Bin b WHERE b.capacity > :requiredVolume")
    List<Bin> findBinsWithAvailableCapacity(@Param("requiredVolume") Integer requiredVolume);

    // Lấy tất cả bin theo shelf
    List<Bin> findByShelfId(Integer shelfId);

    // Tổng capacity của tất cả Bin
    @Query("SELECT SUM(b.capacity) FROM Bin b")
    Integer getTotalBinCapacity();

    // Tìm bin còn sức chứa
    @Query("SELECT b FROM Bin b WHERE b.capacity > " +
            "(SELECT COALESCE(SUM(box.usedCapacity), 0) FROM Box box WHERE box.bin = b)")
    List<Bin> findBinsWithAvailableCapacity();

}
