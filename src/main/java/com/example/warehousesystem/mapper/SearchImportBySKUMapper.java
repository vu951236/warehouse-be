package com.example.warehousesystem.mapper;

import com.example.warehousesystem.dto.response.SearchImportBySKUResponse;
import com.example.warehousesystem.entity.*;

public class SearchImportBySKUMapper {

    public static SearchImportBySKUResponse toImportBySkuResponse(
            ImportOrder order,
            ImportOrderDetail detail,
            String warehouseName
    ) {
        return SearchImportBySKUResponse.builder()
                .importOrderId(order.getId())
                .importDate(order.getCreatedAt().toString())
                .quantity(detail.getQuantity())
                .status(order.getStatus().name())
                .createdBy(order.getCreatedBy().getFullName())
                .warehouseName(warehouseName)
                .build();
    }
}
