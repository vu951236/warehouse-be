package com.example.warehousesystem.mapper;

import com.example.warehousesystem.dto.response.ImportOrderDetailResponse;
import com.example.warehousesystem.entity.ImportOrderDetail;

public class ImportOrderDetailMapper {

    public static ImportOrderDetailResponse toResponse(ImportOrderDetail detail) {
        return ImportOrderDetailResponse.builder()
                .skuId(detail.getSku().getId())
                .skuCode(detail.getSku().getSkuCode())
                .skuName(detail.getSku().getName())
                .quantity(detail.getQuantity())
                .receivedQuantity(detail.getReceivedQuantity())
                .build();
    }
}
