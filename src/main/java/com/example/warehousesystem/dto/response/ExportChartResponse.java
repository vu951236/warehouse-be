package com.example.warehousesystem.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
//Biểu đồ xuất hàng
public class ExportChartResponse {
    private String exportDate;
    private Long totalItems;
    private Long totalOrders;
}
