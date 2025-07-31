package com.example.warehousesystem.mapper;

import com.example.warehousesystem.dto.response.StorageStatusResponse;

public class StorageStatusMapper {

    public static StorageStatusResponse toResponse(
            String warehouseName,
            double usedCapacity,
            int shelfCount,
            double totalBinCount,
            double binCapacity
    ) {
        double binPerShelf = shelfCount == 0 ? 0 : totalBinCount / shelfCount;
        double totalCapacity = shelfCount * binPerShelf * binCapacity;
        double usedPercent = totalCapacity == 0 ? 0 : (usedCapacity / totalCapacity) * 100;
        double freePercent = 100 - usedPercent;

        return StorageStatusResponse.builder()
                .warehouseName(warehouseName)
                .totalCapacity(Math.round(totalCapacity * 100.0) / 100.0)
                .usedCapacity(Math.round(usedCapacity * 100.0) / 100.0)
                .usedPercentage(Math.round(usedPercent * 100.0) / 100.0)
                .freePercentage(Math.round(freePercent * 100.0) / 100.0)
                .build();
    }
}

