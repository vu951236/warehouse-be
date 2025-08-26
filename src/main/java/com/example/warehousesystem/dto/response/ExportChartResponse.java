package com.example.warehousesystem.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
//Biểu đồ xuất hàng
public class ExportChartResponse {
    private String exportDate;
    private Long totalOrders;
    private Long totalItems;
    private Long manualItems;   // số item xuất manual
    private Long haravanItems;  // số item xuất haravan
}
