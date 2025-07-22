package com.example.warehousesystem.repository;

import com.example.warehousesystem.entity.Box;
import com.example.warehousesystem.entity.Item;
import com.example.warehousesystem.entity.SKU;
import com.example.warehousesystem.entity.Item.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {

    Optional<Item> findByBarcode(String barcode);

    List<Item> findByBox(Box box);

    List<Item> findBySku(SKU sku);

    List<Item> findByStatus(Status status);

    List<Item> findBySkuAndStatus(SKU sku, Status status);
}
