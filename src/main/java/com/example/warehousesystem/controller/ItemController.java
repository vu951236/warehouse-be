package com.example.warehousesystem.controller;

import com.example.warehousesystem.Annotation.SystemLog;
import com.example.warehousesystem.dto.request.SearchItemRequest;
import com.example.warehousesystem.dto.request.UpdateItemRequest;
import com.example.warehousesystem.dto.response.ApiResponse;
import com.example.warehousesystem.dto.response.ItemResponse;
import com.example.warehousesystem.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    /**
     * Xóa item theo mã barcode
     */
    @DeleteMapping("/{barcode}")
    @SystemLog(action = "Xóa item", targetTable = "item")
    public ResponseEntity<ApiResponse<Void>> deleteItem(@PathVariable String barcode) {
        itemService.deleteItem(barcode);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .message("Xóa item thành công")
                        .build()
        );
    }

    /**
     * Cập nhật item (vị trí box + trạng thái)
     */
    @PutMapping("/update")
    @SystemLog(action = "Cập nhật item", targetTable = "item")
    public ResponseEntity<ApiResponse<ItemResponse>> updateItem(@RequestBody UpdateItemRequest request) {
        ItemResponse updatedItem = itemService.updateItem(request);
        return ResponseEntity.ok(
                ApiResponse.<ItemResponse>builder()
                        .message("Cập nhật item thành công")
                        .data(updatedItem)
                        .build()
        );
    }

    /**
     * Tìm kiếm item theo điều kiện
     */
    @PostMapping("/search")
    @SystemLog(action = "Tìm kiếm item", targetTable = "item")
    public ResponseEntity<ApiResponse<List<ItemResponse>>> searchItems(@RequestBody SearchItemRequest request) {
        List<ItemResponse> items = itemService.searchItems(request);
        return ResponseEntity.ok(
                ApiResponse.<List<ItemResponse>>builder()
                        .message("Tìm kiếm item thành công")
                        .data(items)
                        .build()
        );
    }

    /**
     * Xuất danh sách item ra file PDF
     */
    @GetMapping("/export/pdf")
    @SystemLog(action = "Xuất item ra PDF", targetTable = "item")
    public ResponseEntity<byte[]> exportItemToPdf() {
        byte[] pdfBytes = itemService.exportItemToPdf();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=items.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
