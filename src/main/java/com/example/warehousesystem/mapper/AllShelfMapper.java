package com.example.warehousesystem.mapper;

import com.example.warehousesystem.dto.response.AllShelfResponse;
import com.example.warehousesystem.entity.Shelf;

public class AllShelfMapper {
    public static AllShelfResponse toResponse(Shelf shelf, Long itemCount) {
        return AllShelfResponse.builder()
                .id(shelf.getId())
                .shelfCode(shelf.getShelfCode())
                .itemCount(itemCount)
                .build();
    }
}
