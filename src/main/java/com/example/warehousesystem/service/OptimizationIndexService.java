package com.example.warehousesystem.service;

import com.example.warehousesystem.dto.request.OptimizationIndexRequest;
import com.example.warehousesystem.dto.response.OptimizationIndexResponse;
import com.example.warehousesystem.mapper.OptimizationIndexMapper;
import com.example.warehousesystem.repository.ExportOrderRepository;
import com.example.warehousesystem.repository.ImportOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OptimizationIndexService {

    private final ImportOrderRepository importOrderRepository;
    private final ExportOrderRepository exportOrderRepository;

    public List<OptimizationIndexResponse> getOptimizationData(OptimizationIndexRequest request) {
        // Lấy dữ liệu nhập kho
        List<Object[]> importStats = importOrderRepository.getImportStatistics(
                request.getWarehouseId(),
                request.getStartDate(),
                request.getEndDate()
        );

        // Lấy dữ liệu xuất kho
        List<Object[]> exportStats = exportOrderRepository.getExportStatistics(
                request.getWarehouseId(),
                request.getStartDate(),
                request.getEndDate()
        );

        // Map theo ngày để dễ kết hợp
        Map<String, Object[]> importMap = importStats.stream()
                .collect(Collectors.toMap(
                        row -> row[0].toString(),   // date
                        row -> row                 // full row
                ));

        Map<String, Object[]> exportMap = exportStats.stream()
                .collect(Collectors.toMap(
                        row -> row[0].toString(),
                        row -> row
                ));

        // Tổng hợp các ngày có trong cả 2 hoặc 1 bảng
        Set<String> allDates = new TreeSet<>();
        allDates.addAll(importMap.keySet());
        allDates.addAll(exportMap.keySet());

        List<OptimizationIndexResponse> result = new ArrayList<>();

        for (String date : allDates) {
            Object[] importRow = importMap.getOrDefault(date, new Object[]{date, 0L, 0L});
            Object[] exportRow = exportMap.getOrDefault(date, new Object[]{date, 0L, 0L});

            Long totalImportOrders = ((Number) importRow[1]).longValue();
            Long totalImportItems = ((Number) importRow[2]).longValue();
            Long totalExportOrders = ((Number) exportRow[1]).longValue();
            Long totalExportItems = ((Number) exportRow[2]).longValue();

            result.add(
                    OptimizationIndexMapper.toResponse(
                            date,
                            totalImportOrders,
                            totalImportItems,
                            totalExportOrders,
                            totalExportItems
                    )
            );
        }

        return result;
    }
}
