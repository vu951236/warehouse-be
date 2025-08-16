package com.example.warehousesystem.mapper;

import com.example.warehousesystem.dto.response.BoxDetailResponse;
import com.example.warehousesystem.entity.Box;

public class BoxDetailMapper {

    public static BoxDetailResponse toResponse(Box box, Long itemCount, Long skuItemCount) {
        double utilizationRate = 0.0;
        if (box.getCapacity() != null && box.getCapacity() > 0) {
             utilizationRate = (double) box.getUsedCapacity() / box.getCapacity() * 100.0;
        }

        return BoxDetailResponse.builder()
                .boxCode(box.getBoxCode())
                .itemCount(itemCount)
                .utilization(utilizationRate)
                .skuName(box.getSku() != null ? box.getSku().getName() : "")
                .skuItemCount(skuItemCount)
                .build();
    }
}
