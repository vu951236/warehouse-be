package com.example.warehousesystem.mapper;

import com.example.warehousesystem.entity.*;

import java.time.LocalDateTime;

public class ItemImportMapper {

    public static ImportOrder toImportOrder(ImportOrder.Source source, String note, User user) {
        return ImportOrder.builder()
                .source(source)
                .status(ImportOrder.Status.confirmed)
                .createdBy(user)
                .createdAt(LocalDateTime.now())
                .note(note)
                .build();
    }

    public static ImportOrderDetail toDetail(ImportOrder order, SKU sku, Integer quantity) {
        return ImportOrderDetail.builder()
                .importOrder(order)
                .sku(sku)
                .quantity(quantity)
                .build();
    }

    public static Item toItem(Box box, SKU sku, String barcode) {
        return Item.builder()
                .box(box)
                .sku(sku)
                .barcode(barcode)
                .status(Item.Status.available)
                .createdAt(LocalDateTime.now())
                .isDeleted(false)
                .build();
    }
}
