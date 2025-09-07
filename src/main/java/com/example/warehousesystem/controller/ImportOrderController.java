package com.example.warehousesystem.controller;

import com.example.warehousesystem.Annotation.SystemLog;
import com.example.warehousesystem.dto.ExcelItemDTO;
import com.example.warehousesystem.dto.request.*;
import com.example.warehousesystem.dto.response.*;
import com.example.warehousesystem.service.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/manage-import-orders")
@RequiredArgsConstructor
public class ImportOrderController {

    private final TempImportExcelService tempImportExcelService;
    private final ImportFromTempService importFromTempService;
    private final ImportScanBarcodeService importScanBarcodeService;
    private final ImportSingleItemByForm importSingleItemByForm;
    private final ImportOrderDetailService importOrderDetailService;
    private final ImportExcelExportService importOrderExportService;
    private final ImportOrderService importOrderService;
    private final ImportTemplateService importTemplateService;
    private final BarcodeSimulationService barcodeSimulationService;
    private final ImportOrderSearchService importOrderSearchService;


    @PostMapping("/search")
    @SystemLog(action = "Tìm kiếm đơn nhập", targetTable = "importorder")
    public ResponseEntity<ApiResponse<List<ImportOrderBoardResponse>>> searchImportOrders(
            @RequestBody ImportOrderSearchRequest request
    ) {
        List<ImportOrderBoardResponse> result = importOrderSearchService.searchImportOrders(request);
        return ResponseEntity.ok(ApiResponse.<List<ImportOrderBoardResponse>>builder()
                .message("Tìm kiếm thành công")
                .data(result)
                .build());
    }

    @PostMapping("/search-merged")
    @SystemLog(action = "Tìm kiếm đơn nhập (gộp dữ liệu)", targetTable = "importorder")
    public ResponseEntity<ApiResponse<List<ImportOrderBoardResponse>>> searchImportOrdersMerged(
            @RequestBody ImportOrderSearch2Request request
    ) {
        List<ImportOrderBoardResponse> result = importOrderSearchService.searchImportOrdersMerged(request);
        return ResponseEntity.ok(ApiResponse.<List<ImportOrderBoardResponse>>builder()
                .message("Tìm kiếm gộp dữ liệu thành công")
                .data(result)
                .build());
    }

    @PostMapping("/upload-barcode-to-temp")
    @SystemLog(action = "Upload barcode lưu vào bảng tạm", targetTable = "importorder_temp")
    public ResponseEntity<ApiResponse<String>> uploadBarcodeToTemp(
            @RequestBody ImportScanBarcodeRequest request
    ) {
        importScanBarcodeService.saveScannedBarcodes(request);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .message("Đã lưu barcode vào bảng tạm thành công")
                .data("success")
                .build());
    }

    @GetMapping("/temp-items")
    @SystemLog(action = "Lấy danh sách bản ghi tạm", targetTable = "importorder_temp")
    public ResponseEntity<ApiResponse<List<TempImportExcelResponse>>> getTempItems() {
        return ResponseEntity.ok(ApiResponse.<List<TempImportExcelResponse>>builder()
                .message("Lấy dữ liệu tạm thành công")
                .data(tempImportExcelService.getTempItemsByUser())
                .build());
    }

    @DeleteMapping("/delete/temp/{id}")
    @SystemLog(action = "Xóa bản ghi tạm", targetTable = "importorder_temp")
    public ResponseEntity<ApiResponse<String>> deleteTempItem(@PathVariable Long id) {
        tempImportExcelService.deleteTempItem(id);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .message("Xóa bản ghi tạm thành công")
                .data("success")
                .build());
    }

    @PostMapping(value = "/upload-excel-to-temp", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SystemLog(action = "Upload Excel lưu vào bảng tạm", targetTable = "importorder_temp")
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
    @SystemLog(action = "Nhập kho từ bảng tạm", targetTable = "importorder")
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
    @SystemLog(action = "Xem lịch sử nhập theo SKU", targetTable = "importorder")
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
    @SystemLog(action = "Xem chi tiết đơn nhập", targetTable = "importorder")
    public ResponseEntity<ApiResponse<List<ImportOrderDetailResponse>>> getImportDetails(
            @PathVariable Integer importOrderId
    ) {
        List<ImportOrderDetailResponse> details = importOrderDetailService.getImportOrderDetails(importOrderId);
        return ResponseEntity.ok(ApiResponse.<List<ImportOrderDetailResponse>>builder()
                .message("Lấy chi tiết đơn nhập thành công")
                .data(details)
                .build());
    }

    @GetMapping("/export-excel")
    @SystemLog(action = "Xuất danh sách đơn nhập ra Excel", targetTable = "importorder")
    public ResponseEntity<Resource> exportImportOrders() throws IOException {
        ByteArrayResource resource = importOrderExportService.exportImportOrders();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=import_orders_excel.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(resource.contentLength())
                .body(resource);
    }

    @GetMapping("/getallImportOrder")
    @SystemLog(action = "Lấy danh sách tất cả đơn nhập", targetTable = "importorder")
    public ResponseEntity<ApiResponse<List<ImportOrderResponse>>> getAllImportOrders() {
        return ResponseEntity.ok(ApiResponse.<List<ImportOrderResponse>>builder()
                .message("Lấy tất cả đơn nhập thành công")
                .data(importOrderService.getAllImportOrders())
                .build());
    }

    @GetMapping("/{orderId}/details")
    @SystemLog(action = "Lấy chi tiết đơn nhập theo ID", targetTable = "importorder")
    public ResponseEntity<ApiResponse<List<ImportOrderDetailResponse>>> getImportOrderDetails(@PathVariable Integer orderId) {
        return ResponseEntity.ok(ApiResponse.<List<ImportOrderDetailResponse>>builder()
                .message("Lấy chi tiết đơn nhập thành công")
                .data(importOrderService.getImportOrderDetailsByOrderId(orderId))
                .build());
    }

    @GetMapping("/allDetails")
    @SystemLog(action = "Lấy toàn bộ chi tiết đơn nhập", targetTable = "importorder")
    public ResponseEntity<ApiResponse<List<ImportOrderBoardResponse>>> getAllImportOrderDetails() {
        return ResponseEntity.ok(ApiResponse.<List<ImportOrderBoardResponse>>builder()
                .message("Lấy toàn bộ chi tiết nhập hàng thành công")
                .data(importOrderService.getAllImportOrderDetails())
                .build());
    }

    @GetMapping("/allDetails/merged-sku")
    @SystemLog(action = "Lấy toàn bộ chi tiết nhập hàng (gộp SKU)", targetTable = "importorder")
    public ResponseEntity<ApiResponse<List<ImportOrderBoardResponse>>> getAllImportOrderDetailsMergedWithSkuList() {
        return ResponseEntity.ok(ApiResponse.<List<ImportOrderBoardResponse>>builder()
                .message("Lấy toàn bộ chi tiết nhập hàng (gộp SKU) thành công")
                .data(importOrderService.getAllImportOrderDetailsMergedWithSkuList())
                .build());
    }

    @PostMapping("/import-single-item")
    @SystemLog(action = "Nhập kho một sản phẩm", targetTable = "importorder")
    public ResponseEntity<ApiResponse<Object>> importSingleItem(@RequestBody ImportSingleItemRequest request) {
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .message("Nhập kho một sản phẩm thành công")
                        .data(importSingleItemByForm.importSingleItemByForm(request))
                        .build()
        );
    }

    @GetMapping("/import-orders/{id}/fullDetail")
    @SystemLog(action = "Xem đầy đủ thông tin đơn nhập theo ID", targetTable = "importorder")
    public ResponseEntity<ApiResponse<ImportOrderFullResponse>> getFullImportOrder(@PathVariable Integer id) {
        return ResponseEntity.ok(
                ApiResponse.<ImportOrderFullResponse>builder()
                        .message("Lấy đầy đủ thông tin đơn nhập thành công")
                        .data(importOrderService.getFullImportOrderById(id))
                        .build()
        );
    }

    @GetMapping("/detail/{detailId}/full")
    @SystemLog(action = "Xem đầy đủ thông tin đơn nhập theo detailId", targetTable = "importorder")
    public ResponseEntity<ApiResponse<ImportOrderFullResponse>> getFullImportOrderByDetailId(
            @PathVariable Integer detailId
    ) {
        ImportOrderFullResponse response = importOrderService.getFullImportOrderByDetailId(detailId);
        return ResponseEntity.ok(
                ApiResponse.<ImportOrderFullResponse>builder()
                        .message("Lấy đầy đủ thông tin đơn nhập từ chi tiết thành công")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/template")
    @SystemLog(action = "Tải xuống template nhập kho", targetTable = "importorder")
    public ResponseEntity<Resource> downloadImportTemplate() throws IOException {
        ByteArrayResource resource = importTemplateService.generateTemplate();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=import_template.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(resource.contentLength())
                .body(resource);
    }

    @PutMapping("/temp/update")
    @SystemLog(action = "Cập nhật bản ghi tạm", targetTable = "importorder_temp")
    public ResponseEntity<ApiResponse<String>> updateTempItem(@RequestBody UpdateTempImportRequest request) {
        tempImportExcelService.updateTempImport(request);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .message("Cập nhật bản ghi tạm thành công")
                .data("success")
                .build());
    }

    @GetMapping("/{id}/simulate-barcode")
    @SystemLog(action = "Giả lập bắn mã barcode", targetTable = "importorder")
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

    @PostMapping("/create")
    @SystemLog(action = "Tạo phiếu nhập", targetTable = "importorder")
    public ResponseEntity<ApiResponse<ImportOrderFullResponse>> createImportOrder(
            @RequestBody ImportOrderRequest request
    ) {
        ImportOrderFullResponse response = importOrderService.createImportOrder(request);
        return ResponseEntity.ok(
                ApiResponse.<ImportOrderFullResponse>builder()
                        .message("Tạo phiếu nhập thành công")
                        .data(response)
                        .build()
        );
    }
}
