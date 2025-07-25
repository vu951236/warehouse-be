package com.example.warehousesystem.mapper;

import com.example.warehousesystem.dto.response.SkuTypeRatioChartResponse;

public class SkuTypeRatioChartMapper {
    public static SkuTypeRatioChartResponse toResponse(String skuName, Long quantity) {
        return SkuTypeRatioChartResponse.builder()
                .skuName(skuName)
                .totalQuantity(quantity)
                .build();
    }
}

