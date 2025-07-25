package com.example.warehousesystem.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExportChartRequest {
    private Integer warehouseId;
    private String fromDate;    // yyyy-MM-dd
    private String toDate;      // yyyy-MM-dd
}
