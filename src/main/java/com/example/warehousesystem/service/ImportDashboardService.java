package com.example.warehousesystem.service;

import com.example.warehousesystem.dto.request.ImportDashboardRequest;
import com.example.warehousesystem.dto.response.ImportChartResponse;
import com.example.warehousesystem.dto.response.ImportKpiResponse;
import com.example.warehousesystem.mapper.ImportChartMapper;
import com.example.warehousesystem.repository.ImportOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImportDashboardService {

    private final ImportOrderRepository importOrderRepository;

    // API KPI
    public ImportKpiResponse getImportKpis(ImportDashboardRequest request) {
        Long totalOrders = importOrderRepository.countConfirmedOrders(
                request.getWarehouseId(), request.getStartDate(), request.getEndDate());

        Long totalItems = importOrderRepository.sumConfirmedItems(
                request.getWarehouseId(), request.getStartDate(), request.getEndDate());

        Long itemsFromFactory = importOrderRepository.sumItemsByFactory(
                request.getWarehouseId(), request.getStartDate(), request.getEndDate(), "factory");

        Long itemsFromReturn = importOrderRepository.sumItemsByReturnGoods(
                request.getWarehouseId(), request.getStartDate(), request.getEndDate(), "returnGoods");

        return ImportKpiResponse.builder()
                .totalImportOrders(totalOrders)
                .totalItemsImported(totalItems)
                .itemsFromFactory(itemsFromFactory)
                .itemsFromReturn(itemsFromReturn)
                .build();
    }

    // API Chart
    public List<ImportChartResponse> getImportChartData(ImportDashboardRequest request) {
        List<Object[]> rawData = importOrderRepository.getImportChartData(
                request.getWarehouseId(), request.getStartDate(), request.getEndDate());

        Map<LocalDate, ImportChartResponse> chartMap = new HashMap<>();
        for (Object[] row : rawData) {
            LocalDate importDate = LocalDate.parse((String) row[0]);
            Long orders = row[1] != null ? ((Number) row[1]).longValue() : 0L;
            Long items = row[2] != null ? ((Number) row[2]).longValue() : 0L;

            chartMap.put(importDate, ImportChartMapper.toResponse(importDate.toString(), items, orders));
        }

        // Fill đủ ngày
        List<ImportChartResponse> chartData = new ArrayList<>();
        LocalDate current = request.getStartDate();
        while (!current.isAfter(request.getEndDate())) {
            ImportChartResponse response = chartMap.getOrDefault(
                    current,
                    ImportChartMapper.toResponse(current.toString(), 0L, 0L)
            );
            chartData.add(response);
            current = current.plusDays(1);
        }

        return chartData;
    }
}
