package com.example.warehousesystem.mapper;

import com.example.warehousesystem.dto.response.StorageStatusResponse;

import java.math.BigDecimal;
import java.math.RoundingMode;

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
                .totalCapacity(round(totalCapacity))
                .usedCapacity(round(usedCapacity))
                .usedPercentage(round(usedPercent))
                .freePercentage(round(freePercent))
                .build();
    }

    private static double round(double value) {
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
