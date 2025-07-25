package com.example.warehousesystem.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OptimizationIndexResponse {
    private String date;
    private Long totalImportOrders;
    private Long totalImportItems;
    private Long totalExportOrders;
    private Long totalExportItems;
    private Double optimizationRate; // Tỉ lệ tối ưu
}
