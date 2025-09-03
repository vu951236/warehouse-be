package com.example.warehousesystem.service;

import com.example.warehousesystem.dto.request.DashboardRequest;
import com.example.warehousesystem.dto.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ImportDashboardService importService;
    private final ExportDashboardService exportService;
    private final StorageDashboardService storageService;
    private final QualityService qualityService;

    public DashboardResponse getDashboard(DashboardRequest req) {
        CompletableFuture<ImportKpiResponse> importFuture =
                CompletableFuture.supplyAsync(() -> importService.getImportKpis(req));
        CompletableFuture<ExportKpiResponse> exportFuture =
                CompletableFuture.supplyAsync(() -> exportService.getKpis(req));
        CompletableFuture<StorageKpiResponse> storageFuture =
                CompletableFuture.supplyAsync(() -> storageService.getKpis(req));
        CompletableFuture<QualityKpiResponse> qualityFuture =
                CompletableFuture.supplyAsync(() -> qualityService.getKpis(req));

        CompletableFuture.allOf(importFuture, exportFuture, storageFuture, qualityFuture).join();

        return DashboardResponse.builder()
                .importKpis(importFuture.join())
                .exportKpis(exportFuture.join())
                .storageKpis(storageFuture.join())
                .qualityKpis(qualityFuture.join())
                .build();
    }
}
