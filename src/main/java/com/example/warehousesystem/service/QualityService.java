package com.example.warehousesystem.service;

import com.example.warehousesystem.dto.request.DashboardRequest;
import com.example.warehousesystem.dto.request.QualityRequest;
import com.example.warehousesystem.dto.response.QualityKpiResponse;
import com.example.warehousesystem.dto.response.QualityTrendResponse;
import com.example.warehousesystem.dto.response.TopDamagedSkuResponse;
import com.example.warehousesystem.entity.Item;
import com.example.warehousesystem.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QualityService {

    private final ItemRepository itemRepository;

    // 1. KPI
    public QualityKpiResponse getKpis(DashboardRequest req) {
        Long totalDamaged = itemRepository.countByStatusAndWarehouseId(
                Item.Status.damaged, req.getWarehouseId(), req.getStartDate(), req.getEndDate()
        );
        Long totalReturned = itemRepository.countByStatusAndWarehouseId(
                Item.Status.returned, req.getWarehouseId(), req.getStartDate(), req.getEndDate()
        );
        Long totalImported = itemRepository.countTotalImported(req.getWarehouseId(), req.getStartDate(), req.getEndDate());

        double damagedPct = totalImported != 0 ? (double) totalDamaged / totalImported * 100 : 0;

        return QualityKpiResponse.builder()
                .totalDamaged(totalDamaged)
                .damagedPercentage(round(damagedPct))
                .totalReturned(totalReturned)
                .build();
    }

    // 2. Line chart
    public List<QualityTrendResponse> getTrend(QualityRequest req) {
        List<Object[]> raw = itemRepository.getDamagedReturnedTrend(
                req.getWarehouseId(), req.getStartDate(), req.getEndDate()
        );

        return raw.stream().map(r -> QualityTrendResponse.builder()
                        .date((LocalDate) r[0])
                        .damaged(((Number) r[1]).longValue())
                        .returned(((Number) r[2]).longValue())
                        .build())
                .filter(e -> e.getDamaged() > 0 || e.getReturned() > 0)
                .collect(Collectors.toList());
    }

    // 3. Top 10 SKU
    public List<TopDamagedSkuResponse> getTopDamagedSku(QualityRequest req) {
        List<Object[]> raw = itemRepository.getTopDamagedSku(req.getWarehouseId(), req.getStartDate(), req.getEndDate());

        return raw.stream().limit(10).map(r -> {
            String skuCode = (String) r[0];
            Long damagedQty = ((Number) r[1]).longValue();
            Long totalSkuImported = ((Number) r[2]).longValue();
            double pct = totalSkuImported != 0 ? (double) damagedQty / totalSkuImported * 100 : 0;

            return TopDamagedSkuResponse.builder()
                    .skuCode(skuCode)
                    .damagedQty(damagedQty)
                    .damagedPercentage(round(pct))
                    .build();
        }).collect(Collectors.toList());
    }

    private double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
