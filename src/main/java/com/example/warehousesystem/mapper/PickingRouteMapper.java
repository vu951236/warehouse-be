package com.example.warehousesystem.mapper;

import com.example.warehousesystem.dto.response.PickingRouteResponse;
import com.example.warehousesystem.entity.Box;

import java.util.List;

public class PickingRouteMapper {

    public static PickingRouteResponse toResponse(Box box, Integer quantityPicked, List<String> barcodes) {
        return PickingRouteResponse.builder()
                .skuCode(box.getSku().getSkuCode())
                .skuName(box.getSku().getName())
                .boxCode("BOX-" + box.getId())
                .binCode(box.getBin().getBinCode())
                .shelfCode(box.getBin().getShelf().getShelfCode())
                .warehouseName(box.getBin().getShelf().getWarehouse().getName())
                .quantityPicked(quantityPicked)
                .barcodes(barcodes)
                .build();
    }
}
