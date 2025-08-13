package com.example.warehousesystem.controller;

import com.example.warehousesystem.dto.request.*;
import com.example.warehousesystem.dto.response.*;
import com.example.warehousesystem.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
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

    @PostMapping("/multiple")
    public ResponseEntity<ApiResponse<List<ExportItemResponse>>> exportMultiple(@RequestBody ExportItemRequest request) {
        List<ExportItemResponse> data = exportMultipleItemsService.exportMultipleItems(request);
        return ResponseEntity.ok(
                ApiResponse.<List<ExportItemResponse>>builder()
                        .message("Xuất nhiều item thành công")
                        .data(data)
                        .build()
        );
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
}
