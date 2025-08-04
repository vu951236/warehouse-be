package com.example.warehousesystem.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
//Biểu đồ xuất hàng
public class ExportChartRequest {
    private Integer warehouseId;
    private String startDate;    // yyyy-MM-dd
    private String endDate;      // yyyy-MM-dd
}
