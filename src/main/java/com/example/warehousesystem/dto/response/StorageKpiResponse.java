package com.example.warehousesystem.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StorageKpiResponse {
    private long totalShelves;
    private long totalBins;
    private long totalBoxes;
}
