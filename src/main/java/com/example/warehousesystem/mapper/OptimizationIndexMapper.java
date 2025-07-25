package com.example.warehousesystem.mapper;

import com.example.warehousesystem.dto.response.OptimizationIndexResponse;

public class OptimizationIndexMapper {
    public static OptimizationIndexResponse toResponse(
            String date,
            Long totalImportOrders,
            Long totalImportItems,
            Long totalExportOrders,
            Long totalExportItems
    ) {
        double optimizationRate = 0.0;
        if (totalImportItems != null && totalImportItems > 0) {
            optimizationRate = (double) totalExportItems / totalImportItems * 100;
        }

        return OptimizationIndexResponse.builder()
                .date(date)
                .totalImportOrders(totalImportOrders)
                .totalImportItems(totalImportItems)
                .totalExportOrders(totalExportOrders)
                .totalExportItems(totalExportItems)
                .optimizationRate(Math.round(optimizationRate * 100.0) / 100.0)
                .build();
    }
}
