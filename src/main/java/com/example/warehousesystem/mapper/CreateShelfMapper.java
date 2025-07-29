package com.example.warehousesystem.mapper;

import com.example.warehousesystem.dto.request.CreateShelfRequest;
import com.example.warehousesystem.dto.response.ShelfResponse;
import com.example.warehousesystem.entity.Shelf;
import com.example.warehousesystem.entity.Warehouse;

public class CreateShelfMapper {

    public static Shelf toEntity(CreateShelfRequest request, Warehouse warehouse) {
        return Shelf.builder()
                .shelfCode(request.getShelfCode())
                .binCount(16) // mặc định 16 bin
                .warehouse(warehouse)
                .build();
    }

    public static ShelfResponse toResponse(Shelf shelf) {
        return ShelfResponse.builder()
                .id(shelf.getId())
                .shelfCode(shelf.getShelfCode())
                .binCount(shelf.getBinCount())
                .warehouseId(shelf.getWarehouse().getId())
                .warehouseName(shelf.getWarehouse().getName())
                .build();
    }

}

