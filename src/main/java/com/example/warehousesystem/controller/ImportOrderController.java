package com.example.warehousesystem.controller;

import com.example.warehousesystem.dto.ExcelItemDTO;
import com.example.warehousesystem.dto.request.*;
import com.example.warehousesystem.dto.response.*;
import com.example.warehousesystem.entity.ImportOrder;
import com.example.warehousesystem.entity.TempImportExcel;
import com.example.warehousesystem.service.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/manage-import-orders")
@RequiredArgsConstructor
public class ImportOrderController {

    private final ImportOrderSearchService importOrderSearchService;
    private final TempImportExcelService tempImportExcelService;
    private final ImportFromTempService importFromTempService;
    private final ImportScanBarcodeService importScanBarcodeService;
    private final ImportSingleItemByForm importSingleItemByForm;
    private final ImportOrderDetailService importOrderDetailService;
    private final ImportExcelExportService importExcelExportService;
    private final ImportOrderService importOrderService;

    /**
     * WMS-16: Tìm kiếm đơn nhập kho
     */
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<List<ImportOrderResponse>>> searchImportOrders(
            @RequestBody ImportOrderSearchRequest request
    ) {
        List<ImportOrderResponse> result = importOrderSearchService.searchImportOrders(request);
        return ResponseEntity.ok(
                ApiResponse.<List<ImportOrderResponse>>builder()
                        .message("Tìm kiếm thành công")
                        .data(result)
                        .build()
        );
    }

    /**
     * WMS-17: Nhập kho 1 hoặc nhiều item bằng form quét mã vạch
     */
    @PostMapping("/import-by-scan")
    public ResponseEntity<ApiResponse<ImportItemsResponse>> importItemsByScan(
            @RequestBody ImportScanBarcodeRequest request
    ) {
        ImportItemsResponse result = importScanBarcodeService.importItemsByScan(request);
        return ResponseEntity.ok(
                ApiResponse.<ImportItemsResponse>builder()
                        .message("Nhập kho bằng form quét mã thành công")
                        .data(result)
                        .build()
        );
    }

    /**
     * WMS-18: Nhập kho nhiều item bằng Excel
     */
    @PostMapping(
            value = "/upload-excel-to-temp",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ApiResponse<String>> uploadExcelToTemp(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId,
            @RequestParam(value = "source", required = false) String source,
            @RequestParam(value = "note", required = false) String note
    ) throws IOException {
        List<ExcelItemDTO> items = parseExcel(file);
        tempImportExcelService.saveTempItems(items, userId, source, note);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .message("Đã lưu dữ liệu vào bảng tạm")
                .data("success")
                .build());
    }

    @GetMapping("/temp-items/{userId}")
    public ApiResponse<List<TempImportExcelResponse>> getTempItems(@PathVariable Long userId) {
        return ApiResponse.<List<TempImportExcelResponse>>builder()
                .message("Lấy dữ liệu tạm thành công")
                .data(tempImportExcelService.getTempItemsByUser(userId))
                .build();
    }

    @PostMapping("/import-from-temp")
    public ApiResponse<ImportItemsResponse> importFromTemp(@RequestBody ImportFromTempRequest request) {
        return ApiResponse.<ImportItemsResponse>builder()
                .message("Nhập kho thành công")
                .data(importFromTempService.importSelected(request.getTempIds()))
                .build();
    }

    /**
     * Đọc dữ liệu từ file Excel, bỏ qua header row
     */
    private List<ExcelItemDTO> parseExcel(MultipartFile file) throws IOException {
        List<ExcelItemDTO> items = new ArrayList<>();
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0); // lấy sheet đầu tiên
            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // bỏ header row
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Cell skuCell = row.getCell(0);
                Cell qtyCell = row.getCell(1);

                if (skuCell == null || qtyCell == null) continue;

                String skuCode = skuCell.getStringCellValue().trim();
                int quantity = (int) qtyCell.getNumericCellValue();

                if (!skuCode.isEmpty() && quantity > 0) {
                    ExcelItemDTO dto = new ExcelItemDTO();
                    dto.setSkuCode(skuCode);
                    dto.setQuantity(quantity);
                    items.add(dto);
                }
            }
        }
        return items;
    }

    /**
     * WMS-22: Xem thông tin nhập theo SKU
     */
    @PostMapping("/search-by-sku")
    public ResponseEntity<ApiResponse<List<SearchImportBySKUResponse>>> getImportHistoryBySku(
            @RequestBody SearchImportBySKURequest request
    ) {
        List<SearchImportBySKUResponse> results = importOrderDetailService.getImportHistoryBySku(request);
        return ResponseEntity.ok(
                ApiResponse.<List<SearchImportBySKUResponse>>builder()
                        .message("Lấy thông tin nhập theo SKU thành công")
                        .data(results)
                        .build()
        );
    }

    /**
     * WMS-26: Xem thông tin các lần nhập kho
     */
    @GetMapping("/{importOrderId}")
    public ResponseEntity<ApiResponse<List<ImportOrderDetailResponse>>> getImportDetails(
            @PathVariable Integer importOrderId
    ) {
        List<ImportOrderDetailResponse> details = importOrderDetailService.getImportOrderDetails(importOrderId);
        return ResponseEntity.ok(
                ApiResponse.<List<ImportOrderDetailResponse>>builder()
                        .message("Lấy chi tiết đơn nhập thành công")
                        .data(details)
                        .build()
        );
    }

    /**
     * WMS-27: Xuất danh sách excel
     */
    @PostMapping("/export-excel")
    public ResponseEntity<InputStreamResource> exportImportExcel(@RequestBody SearchImportBySKURequest request) throws IOException {
        ByteArrayInputStream in = importExcelExportService.exportImportHistoryBySku(request);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=import_history.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }

    @GetMapping("/getallImportOrder")
    public ApiResponse<List<ImportOrderResponse>> getAllImportOrders() {
        return ApiResponse.<List<ImportOrderResponse>>builder()
                .data(importOrderService.getAllImportOrders())
                .build();
    }

    @GetMapping("/{orderId}/details")
    public ApiResponse<List<ImportOrderDetailResponse>> getImportOrderDetails(@PathVariable Integer orderId) {
        return ApiResponse.<List<ImportOrderDetailResponse>>builder()
                .data(importOrderService.getImportOrderDetailsByOrderId(orderId))
                .build();
    }

    @GetMapping("/allDetails")
    public List<ImportOrderBoardResponse> getAllImportOrderDetails() {
        return importOrderService.getAllImportOrderDetails();
    }

    @PostMapping("/import-single-item")
    public ImportItemsResponse importSingleItem(@RequestBody ImportSingleItemRequest request) {
        return importSingleItemByForm.importSingleItemByForm(request);
    }

    @GetMapping("/import-orders/{id}/fullDetail")
    public ResponseEntity<ImportOrderFullResponse> getFullImportOrder(@PathVariable Integer id) {
        return ResponseEntity.ok(importOrderService.getFullImportOrderById(id));
    }

}
