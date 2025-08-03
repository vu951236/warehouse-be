package com.example.warehousesystem.controller;

import com.example.warehousesystem.dto.request.ImportExcelItemRequest;
import com.example.warehousesystem.dto.request.ImportOrderSearchRequest;
import com.example.warehousesystem.dto.request.ImportScanBarcodeRequest;
import com.example.warehousesystem.dto.request.SearchImportBySKURequest;
import com.example.warehousesystem.dto.response.ApiResponse;
import com.example.warehousesystem.dto.response.ImportItemsResponse;
import com.example.warehousesystem.dto.response.ImportOrderResponse;
import com.example.warehousesystem.dto.response.SearchImportBySKUResponse;
import com.example.warehousesystem.service.ImportOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/import-orders")
@RequiredArgsConstructor
public class ImportOrderController {

    private final ImportOrderService importOrderService;

    // WMS-16: Tìm kiếm đơn nhập kho
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<List<ImportOrderResponse>>> search(@RequestBody ImportOrderSearchRequest req) {
        List<ImportOrderResponse> result = importOrderService.searchImportOrders(req);
        return ResponseEntity.ok(ApiResponse.<List<ImportOrderResponse>>builder()
                .data(result)
                .message("Tìm kiếm thành công")
                .build());
    }

    // WMS-17: Nhập kho 1 item bằng form
    @PostMapping("/import-by-scan")
    public ResponseEntity<ApiResponse<ImportItemsResponse>> importScan(@RequestBody ImportScanBarcodeRequest req) {
        ImportItemsResponse result = importOrderService.importSingleItem(req);
        return ResponseEntity.ok(ApiResponse.<ImportItemsResponse>builder()
                .data(result)
                .message("Nhập thành công bằng mã quét")
                .build());
    }

    // WMS-18: Nhập kho nhiều item bằng Excel
    @PostMapping("/import-by-excel")
    public ResponseEntity<ApiResponse<ImportItemsResponse>> importExcel(@RequestBody ImportExcelItemRequest req) {
        ImportItemsResponse result = importOrderService.importFromExcel(req);
        return ResponseEntity.ok(ApiResponse.<ImportItemsResponse>builder()
                .data(result)
                .message("Nhập thành công từ Excel")
                .build());
    }

    //WMS19:
    @GetMapping("/import-orders/template")
    public ResponseEntity<ByteArrayResource> downloadImportTemplate() throws IOException {
        ByteArrayOutputStream out = importOrderService.generateImportTemplate();

        ByteArrayResource resource = new ByteArrayResource(out.toByteArray());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=import-template.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(resource);
    }

    //WMS20:
    @PostMapping("/import-orders/scan-barcode")
    public ResponseEntity<ApiResponse<ImportItemsResponse>> importViaBarcode(
            @RequestBody ImportScanBarcodeRequest request
    ) {
        ImportItemsResponse response = importOrderService.importByBarcode(request);
        return ResponseEntity.ok(ApiResponse.<ImportItemsResponse>builder()
                .message("Nhập kho bằng barcode thành công")
                .data(response)
                .build());
    }

    //WMS22:
    @PostMapping("/search-by-sku")
    public ResponseEntity<ApiResponse<List<SearchImportBySKUResponse>>> getImportHistoryBySku(
            @RequestBody SearchImportBySKURequest request
    ) {
        List<SearchImportBySKUResponse> response = importOrderService.getImportHistoryBySKU(request);
        return ResponseEntity.ok(ApiResponse.<List<SearchImportBySKUResponse>>builder()
                .message("Import history by SKU retrieved successfully")
                .data(response)
                .build());
    }


}
