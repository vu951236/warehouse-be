package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.Bin;
import com.example.warehousesystem.entity.Box;
import com.example.warehousesystem.entity.SKU;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoxRepository extends JpaRepository<Box, Integer> {

    // Tổng usedCapacity trong 1 Bin
    @Query("SELECT SUM(b.usedCapacity) FROM Box b WHERE b.bin.id = :binId")
    Integer getTotalUsedCapacityByBinId(Integer binId);

    // Lấy box theo bin và SKU
    List<Box> findByBinAndSku(Bin bin, SKU sku);

    // Tìm box chứa SKU còn chỗ trống
    List<Box> findBySkuAndUsedCapacityLessThan(SKU sku, Integer capacity);

    // Tìm tất cả box trong bin
    List<Box> findByBin(Bin bin);

    //Tổng usedCapacity đã sử dụng trong tất cả các Box
    @Query("SELECT SUM(b.usedCapacity) FROM Box b")
    Integer getTotalUsedCapacity();

    //Tìm box theo skuid
    List<Box> findBySku_Id(Integer skuId);

    //Tìm box theo binid
    List<Box> findByBin_Id(Integer binId);

    // Tìm thùng chứa 1 loại SKU còn chỗ trống
    @Query("SELECT b FROM Box b WHERE b.sku.id = :skuId AND b.usedCapacity < b.capacity")
    List<Box> findAvailableBoxesBySku(@Param("skuId") Integer skuId);

    // tìm bin nào chứa các id các box nhập vào
    @Query("SELECT b FROM Box b JOIN FETCH b.bin WHERE b.id IN :boxIds")
    List<Box> findBoxesWithBin(@Param("boxIds") List<Integer> boxIds);

}
