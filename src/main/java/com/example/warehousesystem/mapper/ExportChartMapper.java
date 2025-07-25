package com.example.warehousesystem.mapper;

import com.example.warehousesystem.dto.response.ExportChartResponse;

public class ExportChartMapper {
    public static ExportChartResponse toResponse(String date, Long totalOrders, Long totalItems) {
        return ExportChartResponse.builder()
                .exportDate(date)
                .totalItems(totalItems)
                .totalOrders(totalOrders)
                .build();
    }
}
