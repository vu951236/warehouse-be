package com.example.warehousesystem.controller;

import com.example.warehousesystem.dto.request.ImportChartRequest;
import com.example.warehousesystem.dto.response.ImportChartResponse;
import com.example.warehousesystem.service.ImportChartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/charts")
@RequiredArgsConstructor
public class ImportChartController {
    private final ImportChartService importChartService;

    @PostMapping("/import")
    public List<ImportChartResponse> getImportChartData(@RequestBody ImportChartRequest request) {
        return importChartService.getImportChartData(request);
    }
}

