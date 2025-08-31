package com.example.warehousesystem.controller;

import com.example.warehousesystem.Annotation.SystemLog;
import com.example.warehousesystem.dto.request.SearchSkuRequest;
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

    /**
     * Lấy tất cả SKU
     */
    @GetMapping
    @SystemLog(action = "Lấy danh sách SKU", targetTable = "sku")
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

    /**
     * Lấy chi tiết SKU theo ID
     */
    @GetMapping("/getById/{id}")
    @SystemLog(action = "Xem chi tiết SKU", targetTable = "sku")
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

    /**
     * Lấy danh sách SKU cùng số lượng item damaged
     */
    @GetMapping("/damaged")
    @SystemLog(action = "Lấy danh sách SKU lỗi", targetTable = "sku")
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

    /**
     * Lấy chi tiết SKU damaged theo ID
     */
    @GetMapping("/damaged/{skuId}")
    @SystemLog(action = "Xem chi tiết SKU lỗi", targetTable = "sku")
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

    /**
     * Tìm kiếm SKU theo điều kiện
     */
    @PostMapping("/search")
    @SystemLog(action = "Tìm kiếm SKU", targetTable = "sku")
    public ResponseEntity<ApiResponse<List<SkuDetailResponse>>> searchSkus(
            @RequestBody SearchSkuRequest request
    ) {
        List<SkuDetailResponse> results = skuService.searchSkus(request);
        return ResponseEntity.ok(
                ApiResponse.<List<SkuDetailResponse>>builder()
                        .code(200)
                        .message("Tìm kiếm SKU thành công")
                        .data(results)
                        .build()
        );
    }

    /**
     * Tìm kiếm SKU lỗi theo điều kiện
     */
    @PostMapping("/damaged/search")
    @SystemLog(action = "Tìm kiếm SKU lỗi", targetTable = "sku")
    public ResponseEntity<ApiResponse<List<SKUDamagedResponse>>> searchDamagedSkus(
            @RequestBody SearchSkuRequest request
    ) {
        List<SKUDamagedResponse> results = skuService.searchDamagedSkus(request);
        return ResponseEntity.ok(
                ApiResponse.<List<SKUDamagedResponse>>builder()
                        .code(200)
                        .message("Tìm kiếm SKU lỗi thành công")
                        .data(results)
                        .build()
        );
    }

}
