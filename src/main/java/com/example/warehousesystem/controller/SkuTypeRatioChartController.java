package com.example.warehousesystem.controller;

import com.example.warehousesystem.dto.response.SkuTypeRatioChartResponse;
import com.example.warehousesystem.service.SkuTypeRatioChartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/charts")
@RequiredArgsConstructor
public class SkuTypeRatioChartController {

    private final SkuTypeRatioChartService skuTypeRatioChartService;

    @GetMapping("/sku-ratio")
    public List<SkuTypeRatioChartResponse> getSkuRatioChart() {
        return skuTypeRatioChartService.getSkuTypeRatioChart();
    }
}
