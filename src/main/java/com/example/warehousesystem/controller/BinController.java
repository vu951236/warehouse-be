package com.example.warehousesystem.controller;

import com.example.warehousesystem.dto.request.SearchBinRequest;
import com.example.warehousesystem.dto.request.UpdateBinRequest;
import com.example.warehousesystem.dto.response.ApiResponse;
import com.example.warehousesystem.dto.response.BinResponse;
import com.example.warehousesystem.service.BinService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bins")
@RequiredArgsConstructor
public class BinController {

    private final BinService binService;

    /**
     * API tìm kiếm danh sách bin theo điều kiện
     */
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<List<BinResponse>>> searchBins(@RequestBody SearchBinRequest request) {
        List<BinResponse> responses = binService.searchBins(request);
        return ResponseEntity.ok(
                ApiResponse.<List<BinResponse>>builder()
                        .message("Tìm kiếm bin thành công")
                        .data(responses)
                        .build()
        );
    }

    /**
     * API xóa ngăn hàng theo binCode
     */
    @DeleteMapping("/{binCode}")
    public ResponseEntity<ApiResponse<Void>> deleteBin(@PathVariable String binCode) {
        binService.deleteBin(binCode);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .message("Xoá bin thành công")
                        .build()
        );
    }

    /**
     * API export danh sách bin ra PDF
     */
    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportBinToPdf() {
        byte[] pdfBytes = binService.exportBinToPdf();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=bin_list.pdf")
                .body(pdfBytes);
    }

    /**
     * API cập nhật capacity của một bin
     */
    @PutMapping("/update")
    public ResponseEntity<ApiResponse<BinResponse>> updateBin(@RequestBody UpdateBinRequest request) {
        BinResponse response = binService.updateBin(request);
        return ResponseEntity.ok(
                ApiResponse.<BinResponse>builder()
                        .message("Cập nhật bin thành công")
                        .data(response)
                        .build()
        );
    }
}
