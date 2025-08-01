package com.example.warehousesystem.mapper;

import com.example.warehousesystem.dto.request.CreateImportOrderRequest;
import com.example.warehousesystem.dto.response.CreateImportOrderResponse;
import com.example.warehousesystem.entity.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CreateImportOrderMapper {

    public ImportOrder toEntity(CreateImportOrderRequest request, User user) {
        return ImportOrder.builder()
                .source(ImportOrder.Source.valueOf(request.getSource()))
                .status(ImportOrder.Status.draft)
                .createdBy(user)
                .createdAt(LocalDateTime.now())
                .note(request.getNote())
                .build();
    }

    public CreateImportOrderResponse toResponse(ImportOrder importOrder, List<ImportOrderDetail> details) {
        return CreateImportOrderResponse.builder()
                .id(importOrder.getId())
                .source(importOrder.getSource().toString())
                .status(importOrder.getStatus().toString())
                .note(importOrder.getNote())
                .createdAt(importOrder.getCreatedAt())
                .createdBy(importOrder.getCreatedBy().getUsername())
                .details(details.stream().map(detail -> CreateImportOrderResponse.Detail.builder()
                        .skuId(detail.getSku().getId())
                        .skuCode(detail.getSku().getSkuCode())
                        .skuName(detail.getSku().getName())
                        .quantity(detail.getQuantity())
                        .build()).collect(Collectors.toList()))
                .build();
    }
}
