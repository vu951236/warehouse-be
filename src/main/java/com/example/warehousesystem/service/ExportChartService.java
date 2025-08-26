package com.example.warehousesystem.service;

import com.example.warehousesystem.dto.request.ExportChartRequest;
import com.example.warehousesystem.dto.response.ExportChartResponse;
import com.example.warehousesystem.mapper.ExportChartMapper;
import com.example.warehousesystem.repository.ExportOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExportChartService {

    private final ExportOrderRepository exportOrderRepository;

    public List<ExportChartResponse> getExportChartData(ExportChartRequest request) {
        List<Object[]> rawData = exportOrderRepository.getExportChartData(
                request.getWarehouseId(),
                request.getStartDate(),
                request.getEndDate()
        );

        return rawData.stream()
                .map(row -> ExportChartResponse.builder()
                        .exportDate((String) row[0])
                        .totalOrders(row[1] != null ? ((Number) row[1]).longValue() : 0L)
                        .totalItems(row[2] != null ? ((Number) row[2]).longValue() : 0L)
                        .manualItems(row[3] != null ? ((Number) row[3]).longValue() : 0L)
                        .haravanItems(row[4] != null ? ((Number) row[4]).longValue() : 0L)
                        .build())
                .collect(Collectors.toList());
    }

}
