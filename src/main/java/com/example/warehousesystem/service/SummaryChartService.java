package com.example.warehousesystem.service;

import com.example.warehousesystem.dto.request.SummaryChartRequest;
import com.example.warehousesystem.dto.response.SummaryChartResponse;
import com.example.warehousesystem.mapper.SummaryChartMapper;
import com.example.warehousesystem.repository.ExportOrderRepository;
import com.example.warehousesystem.repository.ImportOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SummaryChartService {

    private final ImportOrderRepository importOrderRepository;
    private final ExportOrderRepository exportOrderRepository;

    public List<SummaryChartResponse> getSummaryChart(SummaryChartRequest request) {
        Map<String, SummaryChartResponse> resultMap = new TreeMap<>();

        // Lấy dữ liệu nhập
        List<Object[]> importStats = importOrderRepository.getImportStatistics(
                null, request.getStartDate(), request.getEndDate()
        );

        for (Object[] row : importStats) {
            String dateKey = formatDate(row[0], request.getType());
            Long totalOrders = row[1] != null ? ((Number) row[1]).longValue() : 0L;
            Long totalItems = row[2] != null ? ((Number) row[2]).longValue() : 0L;

            // Nếu đã có tháng này, cộng dồn thêm
            resultMap.putIfAbsent(dateKey, SummaryChartMapper.toResponse(dateKey, 0L, 0L, 0L, 0L));
            SummaryChartResponse current = resultMap.get(dateKey);
            current.setTotalImportOrders(current.getTotalImportOrders() + totalOrders);
            current.setTotalImportItems(current.getTotalImportItems() + totalItems);
        }

        // Lấy dữ liệu xuất
        List<Object[]> exportStats = exportOrderRepository.getExportStatistics(
                null, request.getStartDate(), request.getEndDate()
        );

        for (Object[] row : exportStats) {
            String dateKey = formatDate(row[0], request.getType());
            Long totalOrders = row[1] != null ? ((Number) row[1]).longValue() : 0L;
            Long totalItems = row[2] != null ? ((Number) row[2]).longValue() : 0L;

            resultMap.putIfAbsent(dateKey, SummaryChartMapper.toResponse(dateKey, 0L, 0L, 0L, 0L));
            SummaryChartResponse current = resultMap.get(dateKey);
            current.setTotalExportOrders(current.getTotalExportOrders() + totalOrders);
            current.setTotalExportItems(current.getTotalExportItems() + totalItems);
        }

        return new ArrayList<>(resultMap.values());
    }


    private String formatDate(Object rawDate, String type) {
        String fullDate = rawDate.toString(); // yyyy-MM-dd
        if ("monthly".equalsIgnoreCase(type)) {
            return fullDate.substring(0, 7); // yyyy-MM
        }
        return fullDate;
    }
}
