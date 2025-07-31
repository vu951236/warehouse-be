package com.example.warehousesystem.mapper;

import com.example.warehousesystem.dto.response.SkuResponse;
import com.example.warehousesystem.entity.SKU;

public class SkuMapper {
    public static SkuResponse toSearchResponse(SKU sku) {
        return SkuResponse.builder()
                .id(sku.getId())
                .skuCode(sku.getSkuCode())
                .name(sku.getName())
                .type(sku.getType())
                .color(sku.getColor())
                .size(sku.getSize())
                .unitVolume(sku.getUnitVolume())
                .build();
    }
}
