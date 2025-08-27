package com.example.warehousesystem.controller;

import com.example.warehousesystem.dto.request.*;
import com.example.warehousesystem.dto.response.*;
import com.example.warehousesystem.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final ImportDashboardService importDashboardService;
    private final ExportDashboardService exportDashboardService;
    private final SkuTypeRatioChartService skuTypeRatioChartService;
    private final OptimizationIndexService optimizationIndexService;
    private final StorageDashboardService storageDashboardService;
    private final ReportPdfService reportPdfService;

    @PostMapping("/1.1-import-kpi")
    public ResponseEntity<ApiResponse<ImportKpiResponse>> getImportKpis(
            @RequestBody ImportDashboardRequest request) {
        ImportKpiResponse data = importDashboardService.getImportKpis(request);
        return ResponseEntity.ok(
                ApiResponse.<ImportKpiResponse>builder()
                        .message("Lấy KPI nhập kho thành công")
                        .data(data)
                        .build()
        );
    }

    @PostMapping("/1.2-import-chart")
    public ResponseEntity<ApiResponse<List<ImportChartResponse>>> getImportChart(
            @RequestBody ImportDashboardRequest request) {
        List<ImportChartResponse> data = importDashboardService.getImportChartData(request);
        return ResponseEntity.ok(
                ApiResponse.<List<ImportChartResponse>>builder()
                        .message("Lấy dữ liệu biểu đồ nhập kho thành công")
                        .data(data)
                        .build()
        );
    }


    @PostMapping("2.1-export-kpis")
    public ResponseEntity<ApiResponse<ExportKpiResponse>> getExportKpis(
            @Valid @RequestBody ExportChartRequest request) {

        ExportKpiResponse data = exportDashboardService.getKpis(request);
        return ResponseEntity.ok(
                ApiResponse.<ExportKpiResponse>builder()
                        .message("Lấy KPI xuất kho thành công")
                        .data(data)
                        .build()
        );
    }

    // API 2: Chart (manual/haravan theo ngày)
    @PostMapping("/2.2-export-chart")
    public ResponseEntity<ApiResponse<List<ExportChartResponse>>> getExportChart(
            @Valid @RequestBody ExportChartRequest request) {

        List<ExportChartResponse> data = exportDashboardService.getChart(request);
        return ResponseEntity.ok(
                ApiResponse.<List<ExportChartResponse>>builder()
                        .message("Lấy dữ liệu biểu đồ xuất kho thành công")
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

    @GetMapping("/3.1-storage-kpis")
    public StorageKpiResponse getKpis(@RequestParam(required = false) Integer warehouseId) {
        return storageDashboardService.getKpis(warehouseId);
    }

    @GetMapping("/3.2-storage-donut")
    public StorageDonutResponse getDonut(@RequestParam(required = false) Integer warehouseId) {
        return storageDashboardService.getDonut(warehouseId);
    }

    @GetMapping("/3.3-storage-shelf-chart")
    public List<ShelfCapacityResponse> getShelfChart(@RequestParam(required = false) Integer warehouseId) {
        return storageDashboardService.getShelfChart(warehouseId);
    }

    @GetMapping("/3.4storage-top-bins")
    public List<BinUsageResponse> getTopBins(@RequestParam(required = false) Integer warehouseId) {
        return storageDashboardService.getTopBins(warehouseId);
    }

    @PostMapping(value = "/warehouse-summary-pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportWarehouseReport(@RequestBody StorageStatusRequest request) throws IOException {
        byte[] pdfBytes = reportPdfService.generateWarehouseReportPdf(request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.builder("inline").filename("warehouse-summary.pdf").build());

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
