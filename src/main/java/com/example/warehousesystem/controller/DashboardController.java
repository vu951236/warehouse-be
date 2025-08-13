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

    @PostMapping("/import-chart")
    public ResponseEntity<ApiResponse<List<ImportChartResponse>>> getImportChart(@RequestBody ImportChartRequest request) {
        List<ImportChartResponse> data = importChartService.getImportChartData(request);
        return ResponseEntity.ok(
                ApiResponse.<List<ImportChartResponse>>builder()
                        .message("Lấy dữ liệu biểu đồ nhập kho thành công")
                        .data(data)
                        .build()
        );
    }

    @PostMapping("/export-chart")
    public ResponseEntity<ApiResponse<List<ExportChartResponse>>> getExportChart(@RequestBody ExportChartRequest request) {
        List<ExportChartResponse> data = exportChartService.getExportChartData(request);
        return ResponseEntity.ok(
                ApiResponse.<List<ExportChartResponse>>builder()
                        .message("Lấy dữ liệu biểu đồ xuất kho thành công")
                        .data(data)
                        .build()
        );
    }

    @PostMapping("/summary-chart")
    public ResponseEntity<ApiResponse<List<SummaryChartResponse>>> getSummaryChart(@RequestBody SummaryChartRequest request) {
        List<SummaryChartResponse> data = summaryChartService.getSummaryChart(request);
        return ResponseEntity.ok(
                ApiResponse.<List<SummaryChartResponse>>builder()
                        .message("Lấy dữ liệu biểu đồ tổng kết thành công")
                        .data(data)
                        .build()
        );
    }

    @GetMapping("/sku-type-ratio")
    public ResponseEntity<ApiResponse<List<SkuTypeRatioChartResponse>>> getSkuRatioChart() {
        List<SkuTypeRatioChartResponse> data = skuTypeRatioChartService.getSkuTypeRatioChart();
        return ResponseEntity.ok(
                ApiResponse.<List<SkuTypeRatioChartResponse>>builder()
                        .message("Lấy dữ liệu tỉ lệ loại hàng thành công")
                        .data(data)
                        .build()
        );
    }

    @PostMapping("/optimization-index")
    public ResponseEntity<ApiResponse<List<OptimizationIndexResponse>>> getOptimizationIndex(@RequestBody OptimizationIndexRequest request) {
        List<OptimizationIndexResponse> data = optimizationIndexService.getOptimizationData(request);
        return ResponseEntity.ok(
                ApiResponse.<List<OptimizationIndexResponse>>builder()
                        .message("Lấy dữ liệu chỉ số tối ưu hóa thành công")
                        .data(data)
                        .build()
        );
    }

    @PostMapping("/storage-status")
    public ResponseEntity<ApiResponse<StorageStatusResponse>> getStorageStatus(@RequestBody StorageStatusRequest request) {
        StorageStatusResponse data = storageStatusService.getStorageStatus(request);
        return ResponseEntity.ok(
                ApiResponse.<StorageStatusResponse>builder()
                        .message("Lấy dữ liệu tình trạng sức chứa thành công")
                        .data(data)
                        .build()
        );
    }

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
