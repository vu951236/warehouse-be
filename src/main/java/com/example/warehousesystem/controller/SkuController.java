package com.example.warehousesystem.controller;

import com.example.warehousesystem.dto.response.*;
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

    @GetMapping("/damaged")
    public ResponseEntity<ApiResponse<List<SKUDamagedResponse>>> getAllSKUDamaged() {
        List<SKUDamagedResponse> responses = skuService.getAllSKUDamaged();
        return ResponseEntity.ok(
                ApiResponse.<List<SKUDamagedResponse>>builder()
                        .code(200)
                        .message("Lấy danh sách SKU cùng số item damaged thành công")
                        .data(responses)
                        .build()
        );
    }

    @GetMapping("/damaged/{skuId}")
    public ResponseEntity<ApiResponse<SkuDamagedDetailResponse>> getSkuDamagedDetail(
            @PathVariable Integer skuId
    ) {
        return ResponseEntity.ok(
                ApiResponse.<SkuDamagedDetailResponse>builder()
                        .code(200)
                        .message("Chi tiết SKU lỗi")
                        .data(skuService.getDamagedSkuDetail(skuId))
                        .build()
        );
    }


}
