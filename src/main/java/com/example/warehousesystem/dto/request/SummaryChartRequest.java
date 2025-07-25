package com.example.warehousesystem.dto.request;

import lombok.Data;

@Data
public class SummaryChartRequest {
    private String startDate;  // yyyy-MM-dd
    private String endDate;    // yyyy-MM-dd
    private String type;       // "daily" hoặc "monthly"
}
