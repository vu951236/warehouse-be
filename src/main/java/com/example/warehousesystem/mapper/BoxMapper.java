package com.example.warehousesystem.mapper;

import com.example.warehousesystem.dto.response.BoxResponse;
import com.example.warehousesystem.entity.Box;

public class BoxMapper {

    public static BoxResponse toResponse(Box box) {
        return BoxResponse.builder()
                .id(box.getId())
                .boxCode(box.getBoxCode())
                .binId(box.getBin().getId())
                .binCode(box.getBin().getBinCode())
                .skuId(box.getSku().getId())
                .skuCode(box.getSku().getSkuCode())
                .skuName(box.getSku().getName())
                .capacity(box.getCapacity())
                .usedCapacity(box.getUsedCapacity())
                .build();
    }
}
