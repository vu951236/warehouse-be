package com.example.warehousesystem.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SummaryChartResponse {
    private String date;           // yyyy-MM-dd hoặc yyyy-MM
    private Long totalImportOrders;
    private Long totalImportItems;
    private Long totalExportOrders;
    private Long totalExportItems;
}
