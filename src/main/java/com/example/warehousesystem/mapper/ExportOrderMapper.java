package com.example.warehousesystem.mapper;

import com.example.warehousesystem.dto.response.ExportOrderResponse;
import com.example.warehousesystem.entity.ExportOrder;
import org.springframework.stereotype.Component;

@Component
public class ExportOrderMapper {

    public ExportOrderResponse toResponse(ExportOrder order) {
        return ExportOrderResponse.builder()
                .id(order.getId())
                .exportCode(order.getExportCode())
                .destination(order.getDestination())
                .source(order.getSource().toString())
                .status(order.getStatus().toString())
                .createdBy(order.getCreatedBy().getUsername())
                .createdAt(order.getCreatedAt().atStartOfDay())
                .note(order.getNote())
                .build();
    }
}
