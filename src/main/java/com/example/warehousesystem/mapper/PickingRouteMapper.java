package com.example.warehousesystem.mapper;

import com.example.warehousesystem.dto.response.PickingRouteResponse;
import com.example.warehousesystem.entity.Box;

public class PickingRouteMapper {

    public static PickingRouteResponse toResponse(Box box, Integer quantityPicked) {
        return PickingRouteResponse.builder()
                .skuCode(box.getSku().getSkuCode())
                .skuName(box.getSku().getName())
                .boxCode("BOX-" + box.getId())
                .binCode(box.getBin().getBinCode())
                .shelfCode(box.getBin().getShelf().getShelfCode())
                .warehouseName(box.getBin().getShelf().getWarehouse().getName())
                .quantityPicked(quantityPicked)
                .build();
    }
}
