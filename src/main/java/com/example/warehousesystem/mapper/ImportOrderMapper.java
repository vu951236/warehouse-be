package com.example.warehousesystem.mapper;

import com.example.warehousesystem.dto.response.ImportOrderResponse;
import com.example.warehousesystem.entity.ImportOrder;
import org.springframework.stereotype.Component;

@Component
public class ImportOrderMapper {

    public ImportOrderResponse toResponse(ImportOrder order) {
        return ImportOrderResponse.builder()
                .id(order.getId())
                .source(order.getSource().toString())
                .status(order.getStatus().toString())
                .createdBy(order.getCreatedBy().getUsername())
                .createdAt(order.getCreatedAt())
                .note(order.getNote())
                .build();
    }
}
