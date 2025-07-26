package com.example.warehousesystem.mapper;

import com.example.warehousesystem.dto.response.SearchExportBySKUResponse;
import com.example.warehousesystem.entity.*;

public class SearchExportBySKUMapper {

    public static SearchExportBySKUResponse toExportBySkuResponse(
            ExportOrder order,
            ExportOrderDetail detail,
            String warehouseName
    ) {
        return SearchExportBySKUResponse.builder()
                .exportOrderId(order.getId())
                .exportDate(order.getCreatedAt().toString())
                .quantity(detail.getQuantity())
                .status(order.getStatus().name())
                .createdBy(order.getCreatedBy().getFullName())
                .warehouseName(warehouseName)
                .build();
    }
}
