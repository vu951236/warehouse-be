package com.example.warehousesystem.controller;

import com.example.warehousesystem.dto.request.StorageStatusRequest;
import com.example.warehousesystem.dto.response.OptimizationIndexResponse;
import com.example.warehousesystem.dto.response.SkuTypeRatioChartResponse;
import com.example.warehousesystem.dto.response.StorageStatusResponse;
import com.example.warehousesystem.service.ReportPdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.List;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class WarehouseReportController {

    private final ReportPdfService reportPdfService;

    @PostMapping(value = "/warehouse-summary", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportWarehouseReport(@RequestBody StorageStatusRequest request) {
        byte[] pdfBytes = reportPdfService.generateWarehouseReportPdf(request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.builder("inline").filename("warehouse-summary.pdf").build());

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
