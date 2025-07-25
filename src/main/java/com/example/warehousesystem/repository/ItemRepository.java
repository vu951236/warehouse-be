package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ItemRepository extends JpaRepository<Item, Integer> {

    //Nhập kho item
    @Query("SELECT COUNT(i) > 0 FROM Item i WHERE i.barcode = :barcode")
    boolean existsByBarcode(@Param("barcode") String barcode);

}
