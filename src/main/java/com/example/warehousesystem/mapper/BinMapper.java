package com.example.warehousesystem.mapper;

import com.example.warehousesystem.dto.response.BinResponse;
import com.example.warehousesystem.entity.Bin;

public class BinMapper {

    public static BinResponse toResponse(Bin bin) {
        return BinResponse.builder()
                .id(bin.getId())
                .binCode(bin.getBinCode())
                .capacity(bin.getCapacity())
                .shelfId(bin.getShelf().getId())
                .shelfCode(bin.getShelf().getShelfCode())
                .build();
    }
}
