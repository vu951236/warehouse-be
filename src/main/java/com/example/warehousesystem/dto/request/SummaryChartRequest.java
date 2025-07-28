package com.example.warehousesystem.dto.request;

import lombok.Data;

@Data
//Biểu đồ tổng kết
public class SummaryChartRequest {
    private String startDate;  // yyyy-MM-dd
    private String endDate;    // yyyy-MM-dd
    private String type;       // "daily" hoặc "monthly"
}
