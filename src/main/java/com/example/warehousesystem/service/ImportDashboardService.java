package com.example.warehousesystem.service;

import com.example.warehousesystem.dto.request.ImportDashboardRequest;
import com.example.warehousesystem.dto.response.ImportChartResponse;
import com.example.warehousesystem.dto.response.ImportDashboardResponse;
import com.example.warehousesystem.mapper.ImportChartMapper;
import com.example.warehousesystem.repository.ImportOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
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

        // 2. Chart Data
        List<Object[]> rawData = importOrderRepository.getImportChartData(
                request.getWarehouseId(), request.getStartDate(), request.getEndDate());

        List<ImportChartResponse> chartData = rawData.stream()
                .map(row -> {
                    String importDate = (String) row[0];
                    Long orders = row[1] != null ? ((Number) row[1]).longValue() : 0L;
                    Long items = row[2] != null ? ((Number) row[2]).longValue() : 0L;
                    return ImportChartMapper.toResponse(importDate, items, orders);
                })
                .collect(Collectors.toList());

        return ImportDashboardResponse.builder()
                .totalImportOrders(totalOrders)
                .totalItemsImported(totalItems)
                .itemsFromFactory(itemsFromFactory)
                .itemsFromReturn(itemsFromReturn)
                .chartData(chartData)
                .build();
    }
}
