package com.example.warehousesystem.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardResponse {
    private ImportKpiResponse importKpis;
    private ExportKpiResponse exportKpis;
    private StorageKpiResponse storageKpis;
    private QualityKpiResponse qualityKpis;
}
