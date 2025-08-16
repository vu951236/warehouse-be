package com.example.warehousesystem.controller;

import com.example.warehousesystem.dto.request.*;
import com.example.warehousesystem.dto.response.*;
import com.example.warehousesystem.service.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/admin/export-orders")
@RequiredArgsConstructor
public class ExportOrderController {

    private final ExportOrderService exportOrderService;
    private final ExportMultipleItemsService exportMultipleItemsService;
    private final ExportMultipleItemsByExcelService exportService;
    private final ExportTemplateService templateService;
    private final SearchExportBySKUService searchExportBySKUService;
    private final PickingRouteService pickingRouteService;
    private final UrgentOrderService urgentOrderService;
    private final ExportExcelExportService exportExcelExportService;

    @GetMapping("/getallExportOrder")
    public ResponseEntity<ApiResponse<List<ExportOrderResponse>>> getAllExportOrders() {
        List<ExportOrderResponse> data = exportOrderService.getAllExportOrders();
        return ResponseEntity.ok(
                ApiResponse.<List<ExportOrderResponse>>builder()
                        .message("Lấy tất cả đơn xuất thành công")
                        .data(data)
                        .build()
        );
    }

    @GetMapping("/{orderId}/details")
    public ResponseEntity<ApiResponse<List<ExportOrderDetailResponse>>> getExportOrderDetails(@PathVariable Integer orderId) {
        List<ExportOrderDetailResponse> data = exportOrderService.getExportOrderDetailsByOrderId(orderId);
        return ResponseEntity.ok(
                ApiResponse.<List<ExportOrderDetailResponse>>builder()
                        .message("Lấy chi tiết đơn xuất thành công")
                        .data(data)
                        .build()
        );
    }

    @PostMapping("/search")
    public ResponseEntity<ApiResponse<List<ExportOrderResponse>>> searchExportOrders(@RequestBody ExportOrderSearchRequest request) {
        List<ExportOrderResponse> data = exportOrderService.searchExportOrders(request);
        return ResponseEntity.ok(
                ApiResponse.<List<ExportOrderResponse>>builder()
                        .message("Tìm kiếm đơn xuất thành công")
                        .data(data)
                        .build()
        );
    }

    @PostMapping("/multiple/export-with-route")
    public ResponseEntity<InputStreamResource> exportMultipleWithRouteToExcel(@RequestBody ExportItemRequest request) throws Exception {
        // 1. Xuất hàng và lấy đường đi (service mới nhận List<ExportQueueDTO>)
        ExportWithPickingRouteResponse result = exportMultipleItemsService.exportQueuedItems(request.getItems());

        Workbook workbook = new XSSFWorkbook();

        // --- Sheet 1: Hàng xuất ---
        Sheet sheet1 = workbook.createSheet("Exported Items");
        Row header1 = sheet1.createRow(0);
        header1.createCell(0).setCellValue("STT");
        header1.createCell(1).setCellValue("Mã đơn xuất");
        header1.createCell(2).setCellValue("Ngày xuất");
        header1.createCell(3).setCellValue("SKU");
        header1.createCell(4).setCellValue("Số lượng");

        int rowNum1 = 1;
        for (ExportItemResponse r : result.getExportedItems()) {
            Row row = sheet1.createRow(rowNum1++);
            row.createCell(0).setCellValue(rowNum1 - 1);
            row.createCell(1).setCellValue(r.getExportCode());
            row.createCell(2).setCellValue(r.getExportDateString());
            row.createCell(3).setCellValue(r.getSkuCode());
            row.createCell(4).setCellValue(r.getQuantity());
        }

        // --- Sheet 2: Đường đi lấy hàng ---
        Sheet sheet2 = workbook.createSheet("Picking Route");
        Row header2 = sheet2.createRow(0);
        header2.createCell(0).setCellValue("STT");
        header2.createCell(1).setCellValue("SKU");
        header2.createCell(2).setCellValue("Box");
        header2.createCell(3).setCellValue("Shelf");
        header2.createCell(4).setCellValue("Số lượng cần lấy");
        header2.createCell(5).setCellValue("Barcodes cần lấy");

        int rowNum2 = 1;
        for (PickingRouteResponse p : result.getPickingRoutes()) {
            Row row = sheet2.createRow(rowNum2++);
            row.createCell(0).setCellValue(rowNum2 - 1);
            row.createCell(1).setCellValue(p.getSkuCode());
            row.createCell(2).setCellValue(p.getBoxCode());
            row.createCell(3).setCellValue(p.getShelfCode());
            row.createCell(4).setCellValue(p.getQuantityPicked());
            row.createCell(5).setCellValue(String.join(", ", p.getBarcodes()));
        }

        // --- Xuất file ---
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=exported_items_with_route.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }

    @PostMapping("/multiple/move-to-queue")
    public ResponseEntity<String> moveItemsToQueue(@RequestBody ExportItemRequest request) {
        exportMultipleItemsService.moveItemsToQueue(request.getItems());
        return ResponseEntity.ok("Chuyển item sang queued thành công!");
    }

    @PostMapping("/multiple/move-back-from-queue")
    public ResponseEntity<String> moveItemsBackFromQueue(@RequestBody ExportItemRequest request) {
        exportMultipleItemsService.moveItemsBackFromQueue(request.getItems());
        return ResponseEntity.ok("Chuyển item từ queued về available thành công!");
    }

    @GetMapping("/sku-status")
    public ResponseEntity<List<SKUStatusResponse>> getAllSkuStatus() {
        List<SKUStatusResponse> list = exportMultipleItemsService.getAllSkuStatus();
        return ResponseEntity.ok(list);
    }

    @PostMapping("/excel")
    public ResponseEntity<ApiResponse<List<ExportItemResponse>>> exportItemsByExcel(@RequestBody ExportExcelItemRequest request) {
        List<ExportItemResponse> data = exportService.exportMultipleItemsByExcel(request);
        return ResponseEntity.ok(
                ApiResponse.<List<ExportItemResponse>>builder()
                        .message("Xuất hàng từ file Excel thành công")
                        .data(data)
                        .build()
        );
    }

    @GetMapping("/template/excel")
    public ResponseEntity<byte[]> downloadExportTemplate() {
        byte[] bytes = templateService.createExportExcelTemplate();
        String filename = "export_template.xlsx";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(bytes);
    }

    @PostMapping("/search-by-sku")
    public ResponseEntity<ApiResponse<List<SearchExportBySKUResponse>>> searchExportBySku(@RequestBody SearchExportBySKURequest request) {
        List<SearchExportBySKUResponse> data = searchExportBySKUService.searchBySku(request);
        return ResponseEntity.ok(
                ApiResponse.<List<SearchExportBySKUResponse>>builder()
                        .message("Lấy thông tin xuất theo SKU thành công")
                        .data(data)
                        .build()
        );
    }

    @PostMapping("/optimal")
    public ResponseEntity<ApiResponse<List<PickingRouteResponse>>> getOptimalPickingRoute(@RequestBody PickingRouteRequest request) {
        List<PickingRouteResponse> data = pickingRouteService.getOptimalPickingRoute(request);
        return ResponseEntity.ok(
                ApiResponse.<List<PickingRouteResponse>>builder()
                        .message("Lấy đường đi lấy hàng tối ưu thành công")
                        .data(data)
                        .build()
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<List<UrgentOrderResponse>>> getUrgentOrders(@RequestBody UrgentOrderRequest request) {
        List<UrgentOrderResponse> data = urgentOrderService.getUrgentOrders(request);
        return ResponseEntity.ok(
                ApiResponse.<List<UrgentOrderResponse>>builder()
                        .message("Lấy danh sách đơn hàng gấp thành công")
                        .data(data)
                        .build()
        );
    }

    @PostMapping("/export-excel")
    public ResponseEntity<InputStreamResource> exportExportExcel(@RequestBody SearchExportBySKURequest request) throws IOException {
        ByteArrayInputStream in = exportExcelExportService.exportExportHistoryBySku(request);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=export_history.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }

    @GetMapping("/getAllExportOrderDetails")
    public ResponseEntity<ApiResponse<List<AllExportOrderResponse>>> getAllExportOrderDetails() {
        List<AllExportOrderResponse> data = exportOrderService.getAllExportOrderDetails();
        return ResponseEntity.ok(
                ApiResponse.<List<AllExportOrderResponse>>builder()
                        .message("Lấy tất cả chi tiết đơn xuất thành công")
                        .data(data)
                        .build()
        );
    }

    @GetMapping("/{orderId}/full")
    public ResponseEntity<ApiResponse<ExportOrderFullResponse>> getFullExportOrderById(@PathVariable Integer orderId) {
        ExportOrderFullResponse data = exportOrderService.getFullExportOrderById(orderId);
        return ResponseEntity.ok(
                ApiResponse.<ExportOrderFullResponse>builder()
                        .message("Lấy đầy đủ thông tin đơn xuất thành công")
                        .data(data)
                        .build()
        );
    }

    @GetMapping("/detail/{detailId}/full")
    public ResponseEntity<ApiResponse<ExportOrderFullResponse>> getFullExportOrderByDetailId(@PathVariable Integer detailId) {
        ExportOrderFullResponse data = exportOrderService.getFullExportOrderByDetailId(detailId);
        return ResponseEntity.ok(
                ApiResponse.<ExportOrderFullResponse>builder()
                        .message("Lấy đầy đủ thông tin đơn xuất từ detailId thành công")
                        .data(data)
                        .build()
        );
    }

    @GetMapping("/getAllExportOrder/board")
    public ResponseEntity<ApiResponse<List<ExportOrderBoardResponse>>> getAllExportOrderDetailsMergedWithSkuList() {
        List<ExportOrderBoardResponse> data = exportOrderService.getAllExportOrderDetailsMergedWithSkuList();
        return ResponseEntity.ok(
                ApiResponse.<List<ExportOrderBoardResponse>>builder()
                        .message("Lấy tất cả chi tiết đơn xuất (gộp SKU) thành công")
                        .data(data)
                        .build()
        );
    }

}
