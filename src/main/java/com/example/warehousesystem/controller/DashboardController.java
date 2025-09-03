package com.example.warehousesystem.controller;

import com.example.warehousesystem.Annotation.SystemLog;
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
    private final StorageDashboardService storageDashboardService;
    private final ReportPdfService reportPdfService;
    private final QualityService qualityService;
    private final DashboardService dashboardService;

    @SystemLog(action = "Xem KPI tổng hợp 4 loại", targetTable = "dashboard")
    @PostMapping("/Full4KPI")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard(
            @Valid @RequestBody DashboardRequest request) {

        DashboardResponse data = dashboardService.getDashboard(request);

        return ResponseEntity.ok(
                ApiResponse.<DashboardResponse>builder()
                        .message("Lấy KPI tổng hợp thành công")
                        .data(data)
                        .build()
        );
    }

    // ==================== Import ====================

    @SystemLog(action = "Xem KPI nhập kho", targetTable = "import")
    @PostMapping("/1.1-import-kpi")
    public ResponseEntity<ApiResponse<ImportKpiResponse>> getImportKpis(
            @Valid @RequestBody DashboardRequest request) {
        ImportKpiResponse data = importDashboardService.getImportKpis(request);
        return ResponseEntity.ok(
                ApiResponse.<ImportKpiResponse>builder()
                        .message("Lấy KPI nhập kho thành công")
                        .data(data)
                        .build()
        );
    }

    @SystemLog(action = "Xem biểu đồ nhập kho", targetTable = "import")
    @PostMapping("/1.2-import-chart")
    public ResponseEntity<ApiResponse<List<ImportChartResponse>>> getImportChart(
            @Valid @RequestBody ImportDashboardRequest request) {
        List<ImportChartResponse> data = importDashboardService.getImportChartData(request);
        return ResponseEntity.ok(
                ApiResponse.<List<ImportChartResponse>>builder()
                        .message("Lấy dữ liệu biểu đồ nhập kho thành công")
                        .data(data)
                        .build()
        );
    }

    // ==================== Export ====================

    @SystemLog(action = "Xem KPI xuất kho", targetTable = "export")
    @PostMapping("/2.1-export-kpis")
    public ResponseEntity<ApiResponse<ExportKpiResponse>> getExportKpis(
            @Valid @RequestBody DashboardRequest request) {
        ExportKpiResponse data = exportDashboardService.getKpis(request);
        return ResponseEntity.ok(
                ApiResponse.<ExportKpiResponse>builder()
                        .message("Lấy KPI xuất kho thành công")
                        .data(data)
                        .build()
        );
    }

    @SystemLog(action = "Xem biểu đồ xuất kho", targetTable = "export")
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

    // ==================== Storage ====================

    @SystemLog(action = "Xem KPI lưu trữ", targetTable = "storage")
    @PostMapping("/3.1-storage-kpis")
    public ResponseEntity<ApiResponse<StorageKpiResponse>> getStorageKpis(
            @Valid @RequestBody DashboardRequest request) {
        StorageKpiResponse data = storageDashboardService.getKpis(request);
        return ResponseEntity.ok(
                ApiResponse.<StorageKpiResponse>builder()
                        .message("Lấy KPI lưu trữ thành công")
                        .data(data)
                        .build()
        );
    }

    @SystemLog(action = "Xem donut chart lưu trữ", targetTable = "storage")
    @PostMapping("/3.2-storage-donut")
    public ResponseEntity<ApiResponse<StorageDonutResponse>> getStorageDonut(
            @Valid @RequestBody StorageDashboardRequest request) {
        StorageDonutResponse data = storageDashboardService.getDonut(request);
        return ResponseEntity.ok(
                ApiResponse.<StorageDonutResponse>builder()
                        .message("Lấy dữ liệu donut chart lưu trữ thành công")
                        .data(data)
                        .build()
        );
    }

    @SystemLog(action = "Xem biểu đồ dung lượng theo kệ", targetTable = "storage")
    @PostMapping("/3.3-storage-shelf-chart")
    public ResponseEntity<ApiResponse<List<ShelfCapacityResponse>>> getShelfChart(
            @Valid @RequestBody StorageDashboardRequest request) {
        List<ShelfCapacityResponse> data = storageDashboardService.getShelfChart(request);
        return ResponseEntity.ok(
                ApiResponse.<List<ShelfCapacityResponse>>builder()
                        .message("Lấy dữ liệu dung lượng theo kệ thành công")
                        .data(data)
                        .build()
        );
    }

    @SystemLog(action = "Xem top bin đầy nhất", targetTable = "storage")
    @PostMapping("/3.4-storage-top-bins")
    public ResponseEntity<ApiResponse<List<BinUsageResponse>>> getTopBins(
            @Valid @RequestBody StorageDashboardRequest request) {
        List<BinUsageResponse> data = storageDashboardService.getTopBins(request);
        return ResponseEntity.ok(
                ApiResponse.<List<BinUsageResponse>>builder()
                        .message("Lấy top 10 bin đầy nhất thành công")
                        .data(data)
                        .build()
        );
    }

    // ==================== Quality ====================

    @SystemLog(action = "Xem KPI chất lượng", targetTable = "quality")
    @PostMapping("/4.1-kpi")
    public ResponseEntity<ApiResponse<QualityKpiResponse>> getQualityKpis(
            @Valid @RequestBody DashboardRequest request) {
        QualityKpiResponse data = qualityService.getKpis(request);
        return ResponseEntity.ok(
                ApiResponse.<QualityKpiResponse>builder()
                        .message("Lấy KPI chất lượng thành công")
                        .data(data)
                        .build()
        );
    }

    @SystemLog(action = "Xem trend chất lượng", targetTable = "quality")
    @PostMapping("/4.2-trend")
    public ResponseEntity<ApiResponse<List<QualityTrendResponse>>> getTrend(
            @Valid @RequestBody QualityRequest request) {
        List<QualityTrendResponse> data = qualityService.getTrend(request);
        return ResponseEntity.ok(
                ApiResponse.<List<QualityTrendResponse>>builder()
                        .message("Lấy dữ liệu trend chất lượng thành công")
                        .data(data)
                        .build()
        );
    }

    @SystemLog(action = "Xem top SKU hỏng nhiều nhất", targetTable = "quality")
    @PostMapping("/4.3-top-damaged-sku")
    public ResponseEntity<ApiResponse<List<TopDamagedSkuResponse>>> getTopDamagedSku(
            @Valid @RequestBody QualityRequest request) {
        List<TopDamagedSkuResponse> data = qualityService.getTopDamagedSku(request);
        return ResponseEntity.ok(
                ApiResponse.<List<TopDamagedSkuResponse>>builder()
                        .message("Lấy top 10 SKU hỏng nhiều nhất thành công")
                        .data(data)
                        .build()
        );
    }

    // ==================== Report PDF ====================

    @SystemLog(action = "Xuất báo cáo PDF tổng hợp kho", targetTable = "report")
    @PostMapping(value = "/warehouse-summary-pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportWarehouseReport(
            @Valid @RequestBody StorageStatusRequest request) throws IOException {
        byte[] pdfBytes = reportPdfService.generateWarehouseReportPdf(request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(
                ContentDisposition.builder("inline").filename("warehouse-summary.pdf").build()
        );

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
