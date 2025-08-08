package com.example.warehousesystem.controller;

import com.example.warehousesystem.dto.request.SearchItemRequest;
import com.example.warehousesystem.dto.request.UpdateItemRequest;
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
    public ResponseEntity<Void> deleteItem(@PathVariable String barcode) {
        itemService.deleteItem(barcode);
        return ResponseEntity.noContent().build();
    }

    /**
     * Cập nhật item (vị trí box + trạng thái)
     */
    @PutMapping("/update")
    public ResponseEntity<ItemResponse> updateItem(@RequestBody UpdateItemRequest request) {
        ItemResponse updatedItem = itemService.updateItem(request);
        return ResponseEntity.ok(updatedItem);
    }

    /**
     * Tìm kiếm item theo điều kiện
     */
    @PostMapping("/search")
    public ResponseEntity<List<ItemResponse>> searchItems(@RequestBody SearchItemRequest request) {
        List<ItemResponse> items = itemService.searchItems(request);
        return ResponseEntity.ok(items);
    }

    /**
     * Xuất danh sách item ra file PDF
     */
    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportItemToPdf() {
        byte[] pdfBytes = itemService.exportItemToPdf();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=items.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}