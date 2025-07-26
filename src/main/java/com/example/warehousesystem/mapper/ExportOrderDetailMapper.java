package com.example.warehousesystem.mapper;

import com.example.warehousesystem.dto.response.ExportOrderDetailResponse;
import com.example.warehousesystem.entity.ExportOrderDetail;

public class ExportOrderDetailMapper {

    public static ExportOrderDetailResponse toResponse(ExportOrderDetail detail) {
        return ExportOrderDetailResponse.builder()
                .skuId(detail.getSku().getId())
                .skuCode(detail.getSku().getSkuCode())
                .skuName(detail.getSku().getName())
                .quantity(detail.getQuantity())
                .build();
    }
}
