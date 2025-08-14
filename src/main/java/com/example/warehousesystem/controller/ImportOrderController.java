package com.example.warehousesystem.controller;

import com.example.warehousesystem.dto.ExcelItemDTO;
import com.example.warehousesystem.dto.request.*;
import com.example.warehousesystem.dto.response.*;
import com.example.warehousesystem.service.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
    private final ImportTemplateService importTemplateService;
    private final BarcodeSimulationService barcodeSimulationService;


    @PostMapping("/search")
    public ResponseEntity<ApiResponse<List<ImportOrderResponse>>> searchImportOrders(
            @RequestBody ImportOrderSearchRequest request
    ) {
        List<ImportOrderResponse> result = importOrderSearchService.searchImportOrders(request);
        return ResponseEntity.ok(ApiResponse.<List<ImportOrderResponse>>builder()
                .message("Tìm kiếm thành công")
                .data(result)
                .build());
    }

    @PostMapping("/import-by-scan")
    public ResponseEntity<ApiResponse<ImportItemsResponse>> importItemsByScan(
            @RequestBody ImportScanBarcodeRequest request
    ) {
        ImportItemsResponse result = importScanBarcodeService.importItemsByScan(request);
        return ResponseEntity.ok(ApiResponse.<ImportItemsResponse>builder()
                .message("Nhập kho bằng form quét mã thành công")
                .data(result)
                .build());
    }

    @GetMapping("/temp-items")
    public ResponseEntity<ApiResponse<List<TempImportExcelResponse>>> getTempItems() {
        return ResponseEntity.ok(ApiResponse.<List<TempImportExcelResponse>>builder()
                .message("Lấy dữ liệu tạm thành công")
                .data(tempImportExcelService.getTempItemsByUser())
                .build());
    }

    @PostMapping(value = "/upload-excel-to-temp", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> uploadExcelToTemp(
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        List<ExcelItemDTO> items = parseExcel(file);
        tempImportExcelService.saveTempItems(items);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .message("Đã lưu dữ liệu vào bảng tạm")
                .data("success")
                .build());
    }



    @PostMapping("/import-from-temp")
    public ResponseEntity<ApiResponse<ImportItemsResponse>> importFromTemp(@RequestBody ImportFromTempRequest request) {
        return ResponseEntity.ok(ApiResponse.<ImportItemsResponse>builder()
                .message("Nhập kho thành công")
                .data(importFromTempService.importSelected(request.getTempIds()))
                .build());
    }

    private List<ExcelItemDTO> parseExcel(MultipartFile file) throws IOException {
        List<ExcelItemDTO> items = new ArrayList<>();
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Cell skuCell = row.getCell(0);
                Cell qtyCell = row.getCell(1);
                Cell sourceCell = row.getCell(2);
                Cell noteCell = row.getCell(3);

                if (skuCell == null || qtyCell == null) continue;

                String skuCode = skuCell.getStringCellValue().trim();
                int quantity = (int) qtyCell.getNumericCellValue();
                String source = sourceCell != null ? sourceCell.getStringCellValue().trim() : null;
                String note = noteCell != null ? noteCell.getStringCellValue().trim() : null;

                if (!skuCode.isEmpty() && quantity > 0) {
                    ExcelItemDTO dto = new ExcelItemDTO();
                    dto.setSkuCode(skuCode);
                    dto.setQuantity(quantity);
                    dto.setSource(source);
                    dto.setNote(note);
                    items.add(dto);
                }
            }
        }
        return items;
    }

    @PostMapping("/search-by-sku")
    public ResponseEntity<ApiResponse<List<SearchImportBySKUResponse>>> getImportHistoryBySku(
            @RequestBody SearchImportBySKURequest request
    ) {
        List<SearchImportBySKUResponse> results = importOrderDetailService.getImportHistoryBySku(request);
        return ResponseEntity.ok(ApiResponse.<List<SearchImportBySKUResponse>>builder()
                .message("Lấy thông tin nhập theo SKU thành công")
                .data(results)
                .build());
    }

    @GetMapping("/{importOrderId}")
    public ResponseEntity<ApiResponse<List<ImportOrderDetailResponse>>> getImportDetails(
            @PathVariable Integer importOrderId
    ) {
        List<ImportOrderDetailResponse> details = importOrderDetailService.getImportOrderDetails(importOrderId);
        return ResponseEntity.ok(ApiResponse.<List<ImportOrderDetailResponse>>builder()
                .message("Lấy chi tiết đơn nhập thành công")
                .data(details)
                .build());
    }

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
    public ResponseEntity<ApiResponse<List<ImportOrderResponse>>> getAllImportOrders() {
        return ResponseEntity.ok(ApiResponse.<List<ImportOrderResponse>>builder()
                .message("Lấy tất cả đơn nhập thành công")
                .data(importOrderService.getAllImportOrders())
                .build());
    }

    @GetMapping("/{orderId}/details")
    public ResponseEntity<ApiResponse<List<ImportOrderDetailResponse>>> getImportOrderDetails(@PathVariable Integer orderId) {
        return ResponseEntity.ok(ApiResponse.<List<ImportOrderDetailResponse>>builder()
                .message("Lấy chi tiết đơn nhập thành công")
                .data(importOrderService.getImportOrderDetailsByOrderId(orderId))
                .build());
    }

    @GetMapping("/allDetails")
    public ResponseEntity<ApiResponse<List<ImportOrderBoardResponse>>> getAllImportOrderDetails() {
        return ResponseEntity.ok(ApiResponse.<List<ImportOrderBoardResponse>>builder()
                .message("Lấy toàn bộ chi tiết nhập hàng thành công")
                .data(importOrderService.getAllImportOrderDetails())
                .build());
    }

    @PostMapping("/import-single-item")
    public ResponseEntity<ApiResponse<Object>> importSingleItem(@RequestBody ImportSingleItemRequest request) {
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .message("Nhập kho một sản phẩm thành công")
                        .data(importSingleItemByForm.importSingleItemByForm(request))
                        .build()
        );
    }

    @GetMapping("/import-orders/{id}/fullDetail")
    public ResponseEntity<ApiResponse<Object>> getFullImportOrder(@PathVariable Integer id) {
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .message("Lấy đầy đủ thông tin đơn nhập thành công")
                        .data(importOrderService.getFullImportOrderById(id))
                        .build()
        );
    }

    // WMS-19: Tải xuống mẫu nhập
    @GetMapping("/template")
    public ResponseEntity<Resource> downloadImportTemplate() throws IOException {
        ByteArrayResource resource = importTemplateService.generateTemplate();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=import_template.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(resource.contentLength())
                .body(resource);
    }

    // WMS-21: Chỉnh sửa phiếu nhập tạm
    @PutMapping("/temp/update")
    public ResponseEntity<ApiResponse<String>> updateTempItem(@RequestBody UpdateTempImportRequest request) {
        tempImportExcelService.updateTempImport(request);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .message("Cập nhật bản ghi tạm thành công")
                .data("success")
                .build());
    }

    // WMS-24: Giả lập bắn mã
    @GetMapping("/{id}/simulate-barcode")
    public ResponseEntity<ApiResponse<List<String>>> simulateBarcode(
            @PathVariable("id") Integer importOrderId,
            @RequestParam(defaultValue = "5") int count
    ) {
        List<String> data = barcodeSimulationService.simulateForImportOrder(importOrderId, count);
        return ResponseEntity.ok(ApiResponse.<List<String>>builder()
                .message("Giả lập bắn mã thành công")
                .data(data)
                .build());
    }
}
