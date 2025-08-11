package com.example.warehousesystem.mapper;

import com.example.warehousesystem.dto.response.ImportOrderBoardResponse;
import com.example.warehousesystem.entity.ImportOrderDetail;

public class ImportOrderBoardMapper {

    public static ImportOrderBoardResponse toResponse(ImportOrderDetail detail) {
        return ImportOrderBoardResponse.builder()
                .importCode(detail.getImportOrder().getImportCode())
                .skuCode(detail.getSku().getSkuCode())
                .skuName(detail.getSku().getName())
                .createdAt(detail.getImportOrder().getCreatedAt())
                .quantity(detail.getQuantity())
                .build();
    }
}
