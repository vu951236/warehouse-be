package com.example.warehousesystem.dto.request;

import lombok.Data;

@Data
public class OptimizationIndexRequest {
    private String startDate;
    private String endDate;
    private Integer warehouseId; // có thể null nếu muốn thống kê toàn bộ
}
