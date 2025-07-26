package com.example.warehousesystem.dto.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PickingRouteRequest {

    private List<SKURequest> skuList;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SKURequest {
        private Integer skuId;
        private Integer quantity;
    }
}
