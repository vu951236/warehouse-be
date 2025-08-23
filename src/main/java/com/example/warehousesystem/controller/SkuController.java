package com.example.warehousesystem.controller;

import com.example.warehousesystem.dto.response.SkuDetailResponse;
import com.example.warehousesystem.dto.response.ApiResponse;
import com.example.warehousesystem.dto.response.SkuWithBoxesResponse;
import com.example.warehousesystem.service.SkuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skus")
@RequiredArgsConstructor
public class SkuController {

    private final SkuService skuService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SkuDetailResponse>>> getAllSkus() {
        List<SkuDetailResponse> skus = skuService.getAllSkus();
        return ResponseEntity.ok(
                ApiResponse.<List<SkuDetailResponse>>builder()
                        .code(200)
                        .message("Lấy danh sách SKU thành công")
                        .data(skus)
                        .build()
        );
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<ApiResponse<SkuWithBoxesResponse>> getSkuDetailById(@PathVariable Integer id) {
        SkuWithBoxesResponse skuDetail = skuService.getSkuDetailById(id);
        return ResponseEntity.ok(
                ApiResponse.<SkuWithBoxesResponse>builder()
                        .code(200)
                        .message("Lấy chi tiết SKU thành công")
                        .data(skuDetail)
                        .build()
        );
    }

}
