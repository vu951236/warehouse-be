package com.example.warehousesystem.mapper;

import com.example.warehousesystem.dto.response.ItemResponse;
import com.example.warehousesystem.entity.Item;

public class ItemMapper {

    public static ItemResponse toResponse(Item item) {
        return ItemResponse.builder()
                .id(item.getId())
                .barcode(item.getBarcode())
                .skuCode(item.getSku().getSkuCode())
                .skuName(item.getSku().getName())
                .boxCode(item.getBox().getBoxCode())
                .status(item.getStatus())
                .createdAt(item.getCreatedAt().atStartOfDay())
                .build();
    }
}
