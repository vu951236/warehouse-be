package com.example.warehousesystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ExportWithPickingRouteResponse {
    private List<ExportItemResponse> exportedItems;       // danh sách hàng vừa xuất
    private List<PickingRouteResponse> pickingRoutes;     // đường đi lấy hàng
}
