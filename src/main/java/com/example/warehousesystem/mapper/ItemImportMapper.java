package com.example.warehousesystem.mapper;

import com.example.warehousesystem.entity.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ItemImportMapper {

    public static String generateImportCode() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return "IM" + LocalDateTime.now().format(formatter);
    }

    public static ImportOrder toImportOrder(ImportOrder.Source source, String note, User user) {
        return ImportOrder.builder()
                .source(source)
                .status(ImportOrder.Status.confirmed)
                .importCode(generateImportCode())
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

    public static Item toItem(Box box, SKU sku) {
        return Item.builder()
                .box(box)
                .sku(sku)
                .status(Item.Status.available)
                .createdAt(LocalDateTime.now())
                .isDeleted(false)
                .build();
    }
}
