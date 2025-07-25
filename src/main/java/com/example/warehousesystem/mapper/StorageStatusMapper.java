package com.example.warehousesystem.mapper;

import com.example.warehousesystem.dto.response.StorageStatusResponse;

public class StorageStatusMapper {

    public static StorageStatusResponse toResponse(String warehouseName, double totalCapacity, double usedCapacity) {
        double usedPercent = totalCapacity == 0 ? 0 : (usedCapacity / totalCapacity) * 100;
        double freePercent = 100 - usedPercent;

        return StorageStatusResponse.builder()
                .warehouseName(warehouseName)
                .totalCapacity(totalCapacity)
                .usedCapacity(usedCapacity)
                .usedPercentage(Math.round(usedPercent * 100.0) / 100.0)
                .freePercentage(Math.round(freePercent * 100.0) / 100.0)
                .build();
    }
}
