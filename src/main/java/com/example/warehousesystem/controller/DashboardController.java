package com.example.warehousesystem.controller;

import com.example.warehousesystem.dto.request.*;
import com.example.warehousesystem.dto.response.*;
import com.example.warehousesystem.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final ImportChartService importChartService;
    private final ExportChartService exportChartService;
    private final SummaryChartService summaryChartService;
    private final SkuTypeRatioChartService skuTypeRatioChartService;
    private final OptimizationIndexService optimizationIndexService;
    private final StorageStatusService storageStatusService;
    private final ReportPdfService reportPdfService;


    // WMS-04: Biểu đồ Nhập kho
    @PostMapping("/import-chart")
    public List<ImportChartResponse> getImportChart(@RequestBody ImportChartRequest request) {
        return importChartService.getImportChartData(request);
    }

    // WMS-05: Biểu đồ Xuất kho
    @PostMapping("/export-chart")
    public List<ExportChartResponse> getExportChart(@RequestBody ExportChartRequest request) {
        return exportChartService.getExportChartData(request);
    }

    // WMS-06: Biểu đồ Tổng kết
    @PostMapping("/summary-chart")
    public List<SummaryChartResponse> getSummaryChart(@RequestBody SummaryChartRequest request) {
        return summaryChartService.getSummaryChart(request);
    }

    // WMS-07: Biểu đồ Tỉ lệ loại hàng
    @GetMapping("/sku-type-ratio")
    public List<SkuTypeRatioChartResponse> getSkuRatioChart() {
        return skuTypeRatioChartService.getSkuTypeRatioChart();
    }

    // WMS-09: Biểu đồ Chỉ số tối ưu hóa
    @PostMapping("/optimization-index")
    public List<OptimizationIndexResponse> getOptimizationIndex(@RequestBody OptimizationIndexRequest request) {
        return optimizationIndexService.getOptimizationData(request);
    }

    // WMS-10: Tình trạng sức chứa
    @PostMapping("/storage-status")
    public StorageStatusResponse getStorageStatus(@RequestBody StorageStatusRequest request) {
        return storageStatusService.getStorageStatus(request);
    }

    // WMS-12: Xuất báo cáo tổng hợp kho (PDF)
    @PostMapping(value = "/warehouse-summary-pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportWarehouseReport(@RequestBody StorageStatusRequest request) {
        byte[] pdfBytes = reportPdfService.generateWarehouseReportPdf(request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.builder("inline").filename("warehouse-summary.pdf").build());

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
