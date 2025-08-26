package com.example.warehousesystem.mapper;

import com.example.warehousesystem.entity.*;

import java.time.LocalDate;
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
                .createdAt(LocalDate.now())
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
                .createdAt(LocalDate.now())
                .isDeleted(false)
                .build();
    }
}
