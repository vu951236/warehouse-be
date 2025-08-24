package com.example.warehousesystem.mapper;

import com.example.warehousesystem.dto.response.SKUDamagedResponse;
import com.example.warehousesystem.entity.SKU;

public class SKUDamagedMapper {

    public static SKUDamagedResponse toResponse(SKU sku, Long damagedCount) {
        return SKUDamagedResponse.builder()
                .id(Long.valueOf(sku.getId()))
                .skuCode(sku.getSkuCode())
                .name(sku.getName())
                .size(sku.getSize())
                .color(sku.getColor())
                .type(sku.getType())
                .unitVolume(sku.getUnitVolume())
                .damagedCount(damagedCount)
                .build();
    }
}
