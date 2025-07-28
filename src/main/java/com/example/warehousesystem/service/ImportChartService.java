package com.example.warehousesystem.service;

import com.example.warehousesystem.dto.request.ImportChartRequest;
import com.example.warehousesystem.dto.response.ImportChartResponse;
import com.example.warehousesystem.mapper.ImportChartMapper;
import com.example.warehousesystem.repository.ImportOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImportChartService {

    private final ImportOrderRepository importOrderRepository;

    public List<ImportChartResponse> getImportChartData(ImportChartRequest request) {
        List<Object[]> rawData = importOrderRepository.getImportChartData(
                request.getWarehouseId(),
                request.getFromDate(),
                request.getToDate()
        );

        return rawData.stream()
                .map(row -> {
                    String importDate = (String) row[0];
                    Long totalOrders = row[1] != null ? ((Number) row[1]).longValue() : 0L;
                    Long totalItems = row[2] != null ? ((Number) row[2]).longValue() : 0L;
                    return ImportChartMapper.toResponse(importDate, totalItems, totalOrders);
                })
                .collect(Collectors.toList());
    }
}
