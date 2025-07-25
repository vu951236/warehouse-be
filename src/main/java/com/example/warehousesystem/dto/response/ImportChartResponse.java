package com.example.warehousesystem.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportChartResponse {
    private String importDate;       // yyyy-MM-dd
    private Long totalItems;         // Tổng số lượng hàng nhập trong ngày
    private Long totalOrders;        // Tổng số phiếu nhập trong ngày
}
