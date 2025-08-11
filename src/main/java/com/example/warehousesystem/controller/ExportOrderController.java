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


    // Lấy tất cả đơn xuất
    @GetMapping("/getallExportOrder")
    public ApiResponse<List<ExportOrderResponse>> getAllExportOrders() {
        return ApiResponse.<List<ExportOrderResponse>>builder()
                .data(exportOrderService.getAllExportOrders())
                .build();
    }

    // WMS 39: Lấy chi tiết đơn xuất theo orderId
    @GetMapping("/{orderId}/details")
    public ApiResponse<List<ExportOrderDetailResponse>> getExportOrderDetails(@PathVariable Integer orderId) {
        return ApiResponse.<List<ExportOrderDetailResponse>>builder()
                .data(exportOrderService.getExportOrderDetailsByOrderId(orderId))
                .build();
    }

    /**
     * API: WMS-30 – Tìm kiếm đơn xuất kho
     */
    @PostMapping("/search")
    public ResponseEntity<List<ExportOrderResponse>> searchExportOrders(
            @RequestBody ExportOrderSearchRequest request
    ) {
        List<ExportOrderResponse> result = exportOrderService.searchExportOrders(request);
        return ResponseEntity.ok(result);
    }

    /**
     * API WMS-31 – Xuất kho nhiều item (tick chọn)
     */
    @PostMapping("/multiple")
    public List<ExportItemResponse> exportMultiple(@RequestBody ExportItemRequest request) {
        return exportMultipleItemsService.exportMultipleItems(request);
    }

    /**
     * WMS-32: Xuất hàng bằng dữ liệu từ file Excel
     */
    @PostMapping("/excel")
    public ResponseEntity<List<ExportItemResponse>> exportItemsByExcel(
            @RequestBody ExportExcelItemRequest request
    ) {
        List<ExportItemResponse> responses = exportService.exportMultipleItemsByExcel(request);
        return ResponseEntity.ok(responses);
    }

    /**
     * WMS-33: Tải xuống mẫu xuất Excel
     */
    @GetMapping("/template/excel")
    public ResponseEntity<byte[]> downloadExportTemplate() {
        byte[] bytes = templateService.createExportExcelTemplate();
        String filename = "export_template.xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(bytes);
    }

    /**
     * WMS-34: Xem thông tin xuất theo SKU
     */
    @PostMapping("/search-by-sku")
    public List<SearchExportBySKUResponse> searchExportBySku(@RequestBody SearchExportBySKURequest request) {
        return searchExportBySKUService.searchBySku(request);
    }

    // WMS-35: Đường đi lấy hàng tối ưu
    @PostMapping("/optimal")
    public ResponseEntity<List<PickingRouteResponse>> getOptimalPickingRoute(@RequestBody PickingRouteRequest request) {
        List<PickingRouteResponse> route = pickingRouteService.getOptimalPickingRoute(request);
        return ResponseEntity.ok(route);
    }

    // WMS-36: Ưu tiên đơn hàng gấp
    @PostMapping
    public ResponseEntity<List<UrgentOrderResponse>> getUrgentOrders(@RequestBody UrgentOrderRequest request) {
        List<UrgentOrderResponse> responses = urgentOrderService.getUrgentOrders(request);
        return ResponseEntity.ok(responses);
    }

    /**
     * WMS-40: Xuất danh sách Excel cho lịch sử xuất kho theo SKU
     */
    @PostMapping("/export-excel")
    public ResponseEntity<InputStreamResource> exportExportExcel(@RequestBody SearchExportBySKURequest request) throws IOException, IOException {
        ByteArrayInputStream in = exportExcelExportService.exportExportHistoryBySku(request);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=export_history.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }


}
