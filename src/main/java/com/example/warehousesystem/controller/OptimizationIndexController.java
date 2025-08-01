package com.example.warehousesystem.controller;

import com.example.warehousesystem.dto.request.OptimizationIndexRequest;
import com.example.warehousesystem.dto.response.OptimizationIndexResponse;
import com.example.warehousesystem.service.OptimizationIndexService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/charts")
@RequiredArgsConstructor
public class OptimizationIndexController {

    private final OptimizationIndexService optimizationIndexService;

    @PostMapping("/optimization-index")
    public List<OptimizationIndexResponse> getOptimizationChart(@RequestBody OptimizationIndexRequest request) {
        return optimizationIndexService.getOptimizationData(request);
    }
}
