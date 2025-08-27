package com.example.warehousesystem.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BinUsageResponse {
    private String binCode;
    private double capacity;
    private double usedCapacity;
    private double usedPercentage;
}

