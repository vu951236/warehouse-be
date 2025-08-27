package com.example.warehousesystem.service;

import com.example.warehousesystem.dto.request.ExportChartRequest;
import com.example.warehousesystem.dto.response.ExportChartResponse;
import com.example.warehousesystem.dto.response.ExportKpiResponse;
import com.example.warehousesystem.repository.ExportOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ExportDashboardService {

    private final ExportOrderRepository exportOrderRepository;

    /**
     * Lấy KPI: tổng số đơn xuất confirmed + tổng quantity
     */
    public ExportKpiResponse getKpis(ExportChartRequest req) {
        Long totalOrders = exportOrderRepository.countConfirmedExportOrders(
                req.getWarehouseId(), req.getStartDate(), req.getEndDate()
        );
        Long totalQty = exportOrderRepository.sumConfirmedExportQuantity(
                req.getWarehouseId(), req.getStartDate(), req.getEndDate()
        );

        return ExportKpiResponse.builder()
                .totalConfirmedOrders(totalOrders != null ? totalOrders : 0L)
                .totalConfirmedQuantity(totalQty != null ? totalQty : 0L)
                .build();
    }

    /**
     * Lấy dữ liệu biểu đồ: quantity theo ngày, chia manual / haravan
     */
    public List<ExportChartResponse> getChart(ExportChartRequest req) {
        List<Object[]> rows = exportOrderRepository.getExportChartData(
                req.getWarehouseId(), req.getStartDate(), req.getEndDate()
        );

        // Map date -> response (manualQty, haravanQty)
        Map<LocalDate, ExportChartResponse> byDate = new HashMap<>();
        for (Object[] r : rows) {
            LocalDate date = ((java.sql.Date) r[0]).toLocalDate(); // export_date
            Long manual = ((Number) r[1]).longValue();
            Long haravan = ((Number) r[2]).longValue();

            byDate.putIfAbsent(date, ExportChartResponse.builder()
                    .date(date)
                    .manualQuantity(0L)
                    .haravanQuantity(0L)
                    .build());

            ExportChartResponse e = byDate.get(date);
            e.setManualQuantity(manual);
            e.setHaravanQuantity(haravan);
        }


        // Fill dữ liệu cho tất cả ngày trong khoảng chọn
        List<ExportChartResponse> result = new ArrayList<>();
        for (LocalDate d = req.getStartDate(); !d.isAfter(req.getEndDate()); d = d.plusDays(1)) {
            result.add(byDate.getOrDefault(d,
                    ExportChartResponse.builder()
                            .date(d)
                            .manualQuantity(0L)
                            .haravanQuantity(0L)
                            .build()));
        }

        return result;
    }
}
