package com.example.warehousesystem.controller;

import com.example.warehousesystem.dto.request.CreateBoxRequest;
import com.example.warehousesystem.dto.request.SearchBoxRequest;
import com.example.warehousesystem.dto.request.UpdateBoxRequest;
import com.example.warehousesystem.dto.response.AllBoxResponse;
import com.example.warehousesystem.dto.response.ApiResponse;
import com.example.warehousesystem.dto.response.BoxDetailResponse;
import com.example.warehousesystem.dto.response.BoxResponse;
import com.example.warehousesystem.service.BoxService;
import com.example.warehousesystem.Annotation.SystemLog;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boxes")
@RequiredArgsConstructor
public class BoxController {

    private final BoxService boxService;

    /**
     * Tạo mới box
     */
    @PostMapping("/create")
    @SystemLog(action = "Tạo mới box", targetTable = "box")
    public ResponseEntity<ApiResponse<BoxResponse>> createBox(@RequestBody CreateBoxRequest request) {
        BoxResponse response = boxService.createBox(request);
        return ResponseEntity.ok(
                ApiResponse.<BoxResponse>builder()
                        .message("Tạo box thành công")
                        .data(response)
                        .build()
        );
    }

    /**
     * Cập nhật box
     */
    @PutMapping("/update")
    @SystemLog(action = "Cập nhật box", targetTable = "box")
    public ResponseEntity<ApiResponse<BoxResponse>> updateBox(@RequestBody UpdateBoxRequest request) {
        BoxResponse response = boxService.updateBox(request);
        return ResponseEntity.ok(
                ApiResponse.<BoxResponse>builder()
                        .message("Cập nhật box thành công")
                        .data(response)
                        .build()
        );
    }

    /**
     * Xoá mềm box theo boxCode
     */
    @DeleteMapping("/{boxCode}")
    @SystemLog(action = "Xoá box", targetTable = "box")
    public ResponseEntity<ApiResponse<Void>> deleteBox(@PathVariable String boxCode) {
        boxService.deleteBox(boxCode);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .message("Xoá box thành công")
                        .build()
        );
    }

    /**
     * Tìm kiếm các box theo điều kiện
     */
    @PostMapping("/search")
    @SystemLog(action = "Tìm kiếm box", targetTable = "box")
    public ResponseEntity<ApiResponse<List<BoxResponse>>> searchBoxes(@RequestBody SearchBoxRequest request) {
        List<BoxResponse> responses = boxService.searchBoxs(request);
        return ResponseEntity.ok(
                ApiResponse.<List<BoxResponse>>builder()
                        .message("Tìm kiếm box thành công")
                        .data(responses)
                        .build()
        );
    }

    /**
     * Xuất PDF danh sách box
     */
    @GetMapping("/export/pdf")
    @SystemLog(action = "Xuất danh sách box ra PDF", targetTable = "box")
    public ResponseEntity<byte[]> exportBoxesToPdf() {
        byte[] pdfBytes = boxService.exportBoxToPdf();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=box_list.pdf")
                .body(pdfBytes);
    }

    /**
     * Lấy danh sách box theo binId
     */
    @GetMapping("/bin/{binId}/boxes")
    @SystemLog(action = "Lấy danh sách box theo bin", targetTable = "box")
    public ResponseEntity<ApiResponse<List<AllBoxResponse>>> getBoxesByBin(@PathVariable Integer binId) {
        List<AllBoxResponse> boxes = boxService.getBoxesByBinId(binId);
        return ResponseEntity.ok(
                ApiResponse.<List<AllBoxResponse>>builder()
                        .message("Lấy danh sách box theo bin thành công")
                        .data(boxes)
                        .build()
        );
    }

    /**
     * Lấy chi tiết 1 box
     */
    @GetMapping("/{boxId}/detail")
    @SystemLog(action = "Xem chi tiết box", targetTable = "box")
    public ResponseEntity<ApiResponse<BoxDetailResponse>> getBoxDetail(@PathVariable Integer boxId) {
        BoxDetailResponse response = boxService.getBoxDetail(boxId);
        return ResponseEntity.ok(
                ApiResponse.<BoxDetailResponse>builder()
                        .message("Xem chi tiết box thành công")
                        .data(response)
                        .build()
        );
    }

}
