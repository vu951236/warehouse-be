package com.example.warehousesystem.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShelfCapacityResponse {
    private String shelfName;
    private double totalCapacity;
    private double usedCapacity;
    private double usedPercentage;
}

