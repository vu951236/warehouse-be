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
                .map(row -> {
                    String exportDate = (String) row[0];
                    Long totalOrders = row[1] != null ? ((Number) row[1]).longValue() : 0L;
                    Long totalItems = row[2] != null ? ((Number) row[2]).longValue() : 0L;
                    return ExportChartMapper.toResponse(exportDate, totalOrders, totalItems);
                })
                .collect(Collectors.toList());
    }
}
