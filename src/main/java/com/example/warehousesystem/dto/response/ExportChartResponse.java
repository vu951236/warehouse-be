package com.example.warehousesystem.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExportChartResponse {
    private String exportDate;
    private Long totalItems;
    private Long totalOrders;
}
