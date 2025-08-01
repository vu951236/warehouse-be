package com.example.warehousesystem.controller;

import com.example.warehousesystem.dto.request.ExportChartRequest;
import com.example.warehousesystem.dto.response.ExportChartResponse;
import com.example.warehousesystem.service.ExportChartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/charts")
@RequiredArgsConstructor
public class ExportChartController {

    private final ExportChartService exportChartService;

    @PostMapping("/export")
    public List<ExportChartResponse> getExportChartData(@RequestBody ExportChartRequest request) {
        return exportChartService.getExportChartData(request);
    }
}
