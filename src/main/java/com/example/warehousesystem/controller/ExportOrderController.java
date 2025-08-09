package com.example.warehousesystem.controller;

import com.example.warehousesystem.dto.response.ApiResponse;
import com.example.warehousesystem.dto.response.ExportOrderDetailResponse;
import com.example.warehousesystem.dto.response.ExportOrderResponse;
import com.example.warehousesystem.service.ExportOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/export-orders")
@RequiredArgsConstructor
public class ExportOrderController {

    private final ExportOrderService exportOrderService;

    // Lấy tất cả đơn xuất
    @GetMapping("/getallExportOrder")
    public ApiResponse<List<ExportOrderResponse>> getAllExportOrders() {
        return ApiResponse.<List<ExportOrderResponse>>builder()
                .data(exportOrderService.getAllExportOrders())
                .build();
    }

    // Lấy chi tiết đơn xuất theo orderId
    @GetMapping("/{orderId}/details")
    public ApiResponse<List<ExportOrderDetailResponse>> getExportOrderDetails(@PathVariable Integer orderId) {
        return ApiResponse.<List<ExportOrderDetailResponse>>builder()
                .data(exportOrderService.getExportOrderDetailsByOrderId(orderId))
                .build();
    }
}
