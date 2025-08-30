package com.example.warehousesystem.mapper;

import com.example.warehousesystem.dto.response.SkuDetailResponse;
import com.example.warehousesystem.entity.SKU;
import org.springframework.stereotype.Component;

@Component
public class SkuMapper {

    public SkuDetailResponse toResponse(SKU sku, Long itemCount) {
        if (sku == null) return null;

        return SkuDetailResponse.builder()
                .skuId(sku.getId())
                .skuCode(sku.getSkuCode())
                .itemCount(itemCount)
                .name(sku.getName())
                .size(sku.getSize())
                .color(sku.getColor())
                .type(sku.getType())
                .unitVolume(Double.valueOf(sku.getUnitVolume()))
                .build();
    }
}
