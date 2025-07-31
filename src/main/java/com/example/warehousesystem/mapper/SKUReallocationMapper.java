package com.example.warehousesystem.mapper;

import com.example.warehousesystem.dto.response.SKUReallocationResponse;
import com.example.warehousesystem.entity.SKUPickingFrequency;

public class SKUReallocationMapper {

    public static SKUReallocationResponse toResponse(SKUPickingFrequency frequency, String shelfCode) {
        return SKUReallocationResponse.builder()
                .skuId(frequency.getSku().getId())
                .skuCode(frequency.getSku().getSkuCode())
                .skuName(frequency.getSku().getName())
                .pickCount(frequency.getPickCount())
                .suggestedShelfCode(shelfCode)
                .build();
    }
}
