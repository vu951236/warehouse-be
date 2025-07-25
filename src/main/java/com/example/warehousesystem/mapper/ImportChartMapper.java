package com.example.warehousesystem.mapper;

import com.example.warehousesystem.dto.response.ImportChartResponse;

public class ImportChartMapper {
    public static ImportChartResponse toResponse(String date, Long totalItems, Long totalOrders) {
        return ImportChartResponse.builder()
                .importDate(date)
                .totalItems(totalItems)
                .totalOrders(totalOrders)
                .build();
    }
}
