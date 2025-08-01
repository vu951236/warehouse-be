package com.example.warehousesystem.controller;

import com.example.warehousesystem.dto.request.SummaryChartRequest;
import com.example.warehousesystem.dto.response.SummaryChartResponse;
import com.example.warehousesystem.service.SummaryChartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/charts")
@RequiredArgsConstructor
public class SummaryChartController {

    private final SummaryChartService summaryChartService;

    @PostMapping("/summary")
    public List<SummaryChartResponse> getSummaryChart(@RequestBody SummaryChartRequest request) {
        return summaryChartService.getSummaryChart(request);
    }
}
