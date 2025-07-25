package com.example.warehousesystem.mapper;

import com.example.warehousesystem.dto.response.SummaryChartResponse;

public class SummaryChartMapper {
    public static SummaryChartResponse toResponse(String date,
                                                  Long totalImportOrders,
                                                  Long totalImportItems,
                                                  Long totalExportOrders,
                                                  Long totalExportItems) {
        return SummaryChartResponse.builder()
                .date(date)
                .totalImportOrders(totalImportOrders)
                .totalImportItems(totalImportItems)
                .totalExportOrders(totalExportOrders)
                .totalExportItems(totalExportItems)
                .build();
    }
}
