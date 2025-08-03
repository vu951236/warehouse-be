package com.example.warehousesystem.mapper;

import com.example.warehousesystem.dto.response.UrgentOrderResponse;
import com.example.warehousesystem.entity.ExportOrder;

public class UrgentOrderMapper {
    public static UrgentOrderResponse toResponse(ExportOrder eo) {
        return UrgentOrderResponse.builder()
                .exportOrderId(eo.getId())
                .exportCode(eo.getExportCode())
                .destination(eo.getDestination())
                .status(eo.getStatus().name()) // Enum -> String
                .createdBy(eo.getCreatedBy().getFullName()) // hoặc .getUsername()
                .createdAt(eo.getCreatedAt())
                .note(eo.getNote())
                .build();
    }
}
