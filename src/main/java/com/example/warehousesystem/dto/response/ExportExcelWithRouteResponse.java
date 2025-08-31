package com.example.warehousesystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class ExportExcelWithRouteResponse {
    private String excelFileBase64;
    private List<Map<String, Object>> exportedItems; // Chỉ exportCode, exportDate, note
    private List<PickingRouteResponse> pickingRoutes;
}
