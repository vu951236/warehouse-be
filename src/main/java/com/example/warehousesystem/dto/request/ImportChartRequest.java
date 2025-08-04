package com.example.warehousesystem.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
//Biểu đồ nhập hàng
public class ImportChartRequest {
    private Integer warehouseId;
    private String startDate;       // yyyy-MM-dd
    private String endDate;         // yyyy-MM-dd
}
