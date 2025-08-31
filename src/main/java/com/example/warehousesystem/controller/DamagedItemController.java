package com.example.warehousesystem.controller;

import com.example.warehousesystem.dto.request.TransferItemRequest;
import com.example.warehousesystem.dto.response.ApiResponse;
import com.example.warehousesystem.dto.response.DamagedItemResponse;
import com.example.warehousesystem.service.DamagedItemService;
import com.example.warehousesystem.Annotation.SystemLog;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/damaged-items")
@RequiredArgsConstructor
public class DamagedItemController {

    private final DamagedItemService damagedItemService;

    /**
     * Đánh dấu sản phẩm lỗi
     */
    @PostMapping("/mark")
    @SystemLog(action = "Đánh dấu sản phẩm lỗi", targetTable = "damaged_item")
    public ResponseEntity<ApiResponse<Void>> markDamaged(
            @RequestParam String barcode
    ) {
        damagedItemService.markItemDamaged(barcode);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .code(200)
                        .message("Thêm sản phẩm lỗi vào bảng tạm thành công")
                        .build()
        );
    }

    /**
     * Chuyển sản phẩm lỗi sang bảng chính
     */
    @PostMapping("/transfer")
    @SystemLog(action = "Chuyển sản phẩm lỗi", targetTable = "damaged_item")
    public ResponseEntity<ApiResponse<Void>> transferDamagedItems(
            @RequestBody List<TransferItemRequest> requests
    ) {
        damagedItemService.transferItemsDamaged(requests);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .code(200)
                        .message("Chuyển các sản phẩm lỗi thành công")
                        .build()
        );
    }

    /**
     * Lấy danh sách sản phẩm lỗi
     */
    @GetMapping
    @SystemLog(action = "Lấy danh sách sản phẩm lỗi", targetTable = "damaged_item")
    public ResponseEntity<ApiResponse<List<DamagedItemResponse>>> getAllDamagedItems() {
        List<DamagedItemResponse> responses = damagedItemService.getAllDamagedItems();
        return ResponseEntity.ok(
                ApiResponse.<List<DamagedItemResponse>>builder()
                        .code(200)
                        .message("Lấy danh sách sản phẩm lỗi thành công")
                        .data(responses)
                        .build()
        );
    }

    /**
     * Cập nhật ghi chú sản phẩm lỗi
     */
    @PutMapping("/update-note")
    @SystemLog(action = "Cập nhật ghi chú sản phẩm lỗi", targetTable = "damaged_item")
    public ResponseEntity<ApiResponse<Void>> updateNote(
            @RequestParam String barcode,
            @RequestParam String note
    ) {
        damagedItemService.updateNoteForDamagedItem(barcode, note);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .code(200)
                        .message("Cập nhật ghi chú cho sản phẩm lỗi thành công")
                        .build()
        );
    }

    /**
     * Xóa sản phẩm lỗi
     */
    @DeleteMapping("/delete")
    @SystemLog(action = "Xoá sản phẩm lỗi", targetTable = "damaged_item")
    public ResponseEntity<ApiResponse<Void>> deleteDamagedItem(
            @RequestParam String barcode
    ) {
        damagedItemService.deleteDamagedItem(barcode);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .code(200)
                        .message("Xóa sản phẩm lỗi thành công")
                        .build()
        );
    }

}
