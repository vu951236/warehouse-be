package com.example.warehousesystem.mapper;

import com.example.warehousesystem.dto.response.ShelfResponse;
import com.example.warehousesystem.entity.Shelf;

public class ShelfMapper {

    public static ShelfResponse toResponse(Shelf shelf) {
        return ShelfResponse.builder()
                .id(shelf.getId())
                .shelfCode(shelf.getShelfCode())
                .binCount(shelf.getBinCount())
                .build();
    }
}
