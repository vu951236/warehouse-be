package com.example.warehousesystem.controller;

import com.example.warehousesystem.dto.request.ScanBarcodeRequest;
import com.example.warehousesystem.dto.response.ApiResponse;
import com.example.warehousesystem.dto.response.ScanBarcodeResponse;
import com.example.warehousesystem.service.BarcodeScanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/barcode")
@RequiredArgsConstructor
public class BarcodeScanController {

    private final BarcodeScanService barcodeScanService;

    // WMS-24: Giả lập bắn mã để xem thông tin item
    @PostMapping("/scan")
    public ResponseEntity<ApiResponse<ScanBarcodeResponse>> scan(@RequestBody ScanBarcodeRequest request) {
        ScanBarcodeResponse response = barcodeScanService.scanBarcode(request);
        return ResponseEntity.ok(
                ApiResponse.<ScanBarcodeResponse>builder()
                        .code(200)
                        .message("Đã quét mã thành công")
                        .data(response)
                        .build()
        );
    }
}
