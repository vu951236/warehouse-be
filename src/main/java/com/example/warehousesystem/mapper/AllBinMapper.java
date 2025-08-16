package com.example.warehousesystem.mapper;

import com.example.warehousesystem.dto.response.AllBinResponse;
import com.example.warehousesystem.entity.Bin;

public class AllBinMapper {

    public static AllBinResponse toResponse(Bin bin, Long itemCount) {
        return AllBinResponse.builder()
                .id(bin.getId())
                .binCode(bin.getBinCode())
                .itemCount(itemCount)
                .build();
    }
}
