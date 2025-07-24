package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.Bin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BinRepository extends JpaRepository<Bin, Integer> {

    // Tìm danh sách bin có đủ sức chứa được nhập vào
    @Query("SELECT b FROM Bin b WHERE b.capacity > :requiredVolume")
    List<Bin> findBinsWithCapacityGreaterThan(@Param("requiredVolume") Integer requiredVolume);

    // Lấy tất cả bin theo shelf
    List<Bin> findByShelfId(Integer shelfId);

    // Tổng capacity của tất cả Bin
    @Query("SELECT SUM(b.capacity) FROM Bin b")
    Integer getTotalBinCapacity();

    // Tìm bin còn sức chứa
    @Query("SELECT b FROM Bin b WHERE b.capacity > " +
            "(SELECT COALESCE(SUM(box.usedCapacity), 0) FROM Box box WHERE box.bin = b)")
    List<Bin> findBinsWithAvailableCapacity();

    // tìm shelf chứa id các bin nhập vào
    @Query("SELECT b FROM Bin b JOIN FETCH b.shelf WHERE b.id IN :binIds")
    List<Bin> findBinsWithShelf(@Param("binIds") List<Integer> binIds);

    long countByShelfId(Integer shelfId); // Đếm số bin trong 1 kệ

    void deleteByShelfId(Integer shelfId); // Xóa luôn bin khi xóa kệ

    // Tìm bin theo mã
    List<Bin> findByBinCodeContainingIgnoreCase(String binCode);
}
