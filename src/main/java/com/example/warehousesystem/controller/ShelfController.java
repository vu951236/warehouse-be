package com.example.warehousesystem.controller;

import com.example.warehousesystem.dto.request.CreateShelfRequest;
import com.example.warehousesystem.dto.request.DeleteShelfRequest;
import com.example.warehousesystem.dto.request.SearchShelfRequest;
import com.example.warehousesystem.dto.response.*;
import com.example.warehousesystem.service.ShelfService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/shelves")
@RequiredArgsConstructor
public class ShelfController {

    private final ShelfService shelfService;

    @PostMapping("/search")
    public ResponseEntity<ApiResponse<List<ShelfResponse>>> searchShelves(@RequestBody SearchShelfRequest request) {
        List<ShelfResponse> shelves = shelfService.searchShelves(request);
        return ResponseEntity.ok(
                ApiResponse.<List<ShelfResponse>>builder()
                        .message("Tìm kiếm kệ hàng thành công")
                        .data(shelves)
                        .build()
        );
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<ShelfResponse>> createShelf(@Valid @RequestBody CreateShelfRequest request) {
        ShelfResponse response = shelfService.createShelf(request);
        return ResponseEntity.ok(
                ApiResponse.<ShelfResponse>builder()
                        .message("Tạo kệ hàng thành công")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<String>> deleteShelf(@RequestBody DeleteShelfRequest request) {
        shelfService.deleteShelf(request.getShelfCode());
        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .message("Xóa kệ hàng thành công")
                        .data("success")
                        .build()
        );
    }

    @GetMapping("/pdf")
    public void downloadShelvesPdf(HttpServletResponse response) throws IOException {
        byte[] pdfContent = shelfService.exportShelvesToPdf();
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=shelves.pdf");
        response.getOutputStream().write(pdfContent);
        response.getOutputStream().flush();
    }




    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<AllShelfResponse>>> getAllShelves() {
        List<AllShelfResponse> shelves = shelfService.getAllShelves();

        return ResponseEntity.ok(
                ApiResponse.<List<AllShelfResponse>>builder()
                        .message("Lấy toàn bộ kệ hàng thành công")
                        .data(shelves)
                        .build()
        );
    }

    @GetMapping("/{shelfId}/detail")
    public ResponseEntity<ApiResponse<ShelfDetailResponse>> getShelfDetail(@PathVariable Integer shelfId) {
        ShelfDetailResponse detail = shelfService.getShelfDetail(shelfId);
        return ResponseEntity.ok(
                ApiResponse.<ShelfDetailResponse>builder()
                        .message("Lấy chi tiết kệ hàng thành công")
                        .data(detail)
                        .build()
        );
    }


}
