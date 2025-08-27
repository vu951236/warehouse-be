package com.example.warehousesystem.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StorageDonutResponse {
    private double totalCapacity;
    private double usedCapacity;
    private double usedPercentage;
    private double freePercentage;
}

