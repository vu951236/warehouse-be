package com.example.warehousesystem.service;

import com.example.warehousesystem.dto.request.ImportDashboardRequest;
import com.example.warehousesystem.dto.response.ImportChartResponse;
import com.example.warehousesystem.dto.response.ImportDashboardResponse;
import com.example.warehousesystem.mapper.ImportChartMapper;
import com.example.warehousesystem.repository.ImportOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImportDashboardService {

    private final ImportOrderRepository importOrderRepository;

    public ImportDashboardResponse getImportDashboardData(ImportDashboardRequest request) {
        // 1. KPI Cards
        Long totalOrders = importOrderRepository.countConfirmedOrders(
                request.getWarehouseId(), request.getStartDate(), request.getEndDate());

        Long totalItems = importOrderRepository.sumConfirmedItems(
                request.getWarehouseId(), request.getStartDate(), request.getEndDate());

        Long itemsFromFactory = importOrderRepository.sumItemsByFactory(
                request.getWarehouseId(), request.getStartDate(), request.getEndDate(), "factory");

        Long itemsFromReturn = importOrderRepository.sumItemsByReturnGoods(
                request.getWarehouseId(), request.getStartDate(), request.getEndDate(), "returnGoods");

        // 2. Chart Data (raw từ DB)
        List<Object[]> rawData = importOrderRepository.getImportChartData(
                request.getWarehouseId(), request.getStartDate(), request.getEndDate());

        // Map để dễ tra cứu
        Map<LocalDate, ImportChartResponse> chartMap = new HashMap<>();
        for (Object[] row : rawData) {
            LocalDate importDate = LocalDate.parse((String) row[0]); // row[0] là yyyy-MM-dd
            Long orders = row[1] != null ? ((Number) row[1]).longValue() : 0L;
            Long items = row[2] != null ? ((Number) row[2]).longValue() : 0L;

            chartMap.put(importDate, ImportChartMapper.toResponse(importDate.toString(), items, orders));
        }

        // Fill liên tiếp theo từng ngày
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

        return ImportDashboardResponse.builder()
                .totalImportOrders(totalOrders)
                .totalItemsImported(totalItems)
                .itemsFromFactory(itemsFromFactory)
                .itemsFromReturn(itemsFromReturn)
                .chartData(chartData)
                .build();
    }
}
