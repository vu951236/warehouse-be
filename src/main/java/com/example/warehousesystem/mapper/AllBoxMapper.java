package com.example.warehousesystem.mapper;

import com.example.warehousesystem.dto.response.AllBoxResponse;
import com.example.warehousesystem.entity.Box;

public class AllBoxMapper {

    public static AllBoxResponse toResponse(Box box, Long itemCount) {
        return AllBoxResponse.builder()
                .id(box.getId())
                .boxCode(box.getBoxCode())
                .itemCount(itemCount)
                .build();
    }
}
