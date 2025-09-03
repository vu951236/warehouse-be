package com.example.warehousesystem.service;

import com.example.warehousesystem.dto.request.DashboardRequest;
import com.example.warehousesystem.dto.request.StorageDashboardRequest;
import com.example.warehousesystem.dto.response.*;
import com.example.warehousesystem.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StorageDashboardService {

    private final ShelfRepository shelfRepository;
    private final BinRepository binRepository;
    private final BoxRepository boxRepository;

    // 1. KPI
    public StorageKpiResponse getKpis(DashboardRequest req) {
        long shelfCount = shelfRepository.countByWarehouseId(req.getWarehouseId());
        long binCount = binRepository.countByWarehouseId(req.getWarehouseId());
        long boxCount = boxRepository.countByWarehouseId(req.getWarehouseId());

        return StorageKpiResponse.builder()
                .totalShelves(shelfCount)
                .totalBins(binCount)
                .totalBoxes(boxCount)
                .build();
    }

    // 2. Donut chart
    public StorageDonutResponse getDonut(StorageDashboardRequest req) {
        Double totalCapacity = binRepository.sumCapacityByWarehouseId(req.getWarehouseId());
        Double usedCapacity = binRepository.sumUsedCapacityByWarehouseId(req.getWarehouseId());

        if (totalCapacity == null) totalCapacity = 0.0;
        if (usedCapacity == null) usedCapacity = 0.0;

        double usedPct = totalCapacity == 0 ? 0 : (usedCapacity / totalCapacity) * 100;
        double freePct = 100 - usedPct;

        return StorageDonutResponse.builder()
                .totalCapacity(round(totalCapacity))
                .usedCapacity(round(usedCapacity))
                .usedPercentage(round(usedPct))
                .freePercentage(round(freePct))
                .build();
    }

    // 3. Bar chart theo shelf
    public List<ShelfCapacityResponse> getShelfChart(StorageDashboardRequest req) {
        List<Object[]> rows = shelfRepository.getShelfCapacity(req.getWarehouseId());

        return rows.stream().map(r -> {
            String shelfName = (String) r[0];
            double totalCapacity = ((Number) r[1]).doubleValue();
            double usedCapacity = ((Number) r[2]).doubleValue();
            double usedPct = totalCapacity == 0 ? 0 : (usedCapacity / totalCapacity) * 100;

            return ShelfCapacityResponse.builder()
                    .shelfName(shelfName)
                    .totalCapacity(round(totalCapacity))
                    .usedCapacity(round(usedCapacity))
                    .usedPercentage(round(usedPct))
                    .build();
        }).collect(Collectors.toList());
    }

    // 4. Top 10 bin đầy nhất
    public List<BinUsageResponse> getTopBins(StorageDashboardRequest req) {
        List<Object[]> rows = binRepository.findTop10ByUsage(req.getWarehouseId());

        return rows.stream().map(r -> {
                    String code = (String) r[0];
                    double capacity = ((Number) r[1]).doubleValue();
                    double used = ((Number) r[2]).doubleValue();
                    double usedPct = capacity == 0 ? 0 : (used / capacity) * 100;

                    return BinUsageResponse.builder()
                            .binCode(code)
                            .capacity(round(capacity))
                            .usedCapacity(round(used))
                            .usedPercentage(round(usedPct))
                            .build();
                })
                .limit(10) // chỉ lấy 10 bin
                .collect(Collectors.toList());
    }

    private double round(double v) {
        return BigDecimal.valueOf(v).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
