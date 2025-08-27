package com.example.warehousesystem.mapper;

import com.example.warehousesystem.dto.response.AllExportOrderResponse;
import com.example.warehousesystem.dto.response.ExportOrderBoardResponse;
import com.example.warehousesystem.dto.response.ExportOrderFullResponse;
import com.example.warehousesystem.entity.ExportOrder;
import com.example.warehousesystem.entity.ExportOrderDetail;
import com.example.warehousesystem.entity.SKU;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ExportOrderFullMapper {

    // Map 1 detail sang AllExportOrderResponse (dùng cho danh sách rút gọn)
    public static AllExportOrderResponse toAllResponse(ExportOrderDetail detail) {
        return AllExportOrderResponse.builder()
                .id(detail.getId())
                .exportCode(detail.getExportOrder().getExportCode())
                .skuCode(detail.getSku().getSkuCode())
                .productName(detail.getSku().getName())
                .exportDate(detail.getExportOrder().getCreatedAt().atStartOfDay())
                .quantity(detail.getQuantity())
                .build();
    }

    // Map 1 ExportOrder (với nhiều details) sang ExportOrderFullResponse
    public static ExportOrderFullResponse toFullResponse(ExportOrder exportOrder, List<ExportOrderDetail> details) {
        return ExportOrderFullResponse.builder()
                .id(exportOrder.getId())
                .exportCode(exportOrder.getExportCode())
                .source(exportOrder.getSource().toString())
                .status(exportOrder.getStatus().toString())
                .createdBy(exportOrder.getCreatedBy().getUsername())
                .createdAt(exportOrder.getCreatedAt().atStartOfDay())
                .note(exportOrder.getNote())
                .details(details.stream().map(ExportOrderFullMapper::toDetailItem).collect(Collectors.toList()))
                .build();
    }

    // Map từ ExportOrderDetail sang ExportOrderDetailItem
    public static ExportOrderFullResponse.ExportOrderDetailItem toDetailItem(ExportOrderDetail detail) {
        SKU sku = detail.getSku();
        return ExportOrderFullResponse.ExportOrderDetailItem.builder()
                .id(detail.getId())
                .skuCode(sku.getSkuCode())
                .skuName(sku.getName())
                .size(sku.getSize())
                .color(sku.getColor())
                .type(sku.getType())
                .unitVolume(Double.valueOf(sku.getUnitVolume()))
                .quantity(detail.getQuantity())
                .build();
    }

    // Map từ 1 detail riêng lẻ sang ExportOrderFullResponse (dùng cho getFullExportOrderByDetailId)
    public static ExportOrderFullResponse toFullResponse(ExportOrderDetail detail) {
        ExportOrder exportOrder = detail.getExportOrder();
        return ExportOrderFullResponse.builder()
                .id(exportOrder.getId())
                .exportCode(exportOrder.getExportCode())
                .source(exportOrder.getSource().toString())
                .status(exportOrder.getStatus().toString())
                .createdBy(exportOrder.getCreatedBy().getUsername())
                .createdAt(exportOrder.getCreatedAt().atStartOfDay())
                .note(exportOrder.getNote())
                .details(Collections.singletonList(toDetailItem(detail)))
                .build();
    }

    public static ExportOrderBoardResponse toBoardResponse(List<ExportOrderDetail> orderDetails) {
        ExportOrderDetail first = orderDetails.getFirst();

        String allSkuCodes = orderDetails.stream()
                .map(d -> d.getSku().getSkuCode())
                .distinct()
                .collect(Collectors.joining(", "));

        String allSkuNames = orderDetails.stream()
                .map(d -> d.getSku().getName())
                .distinct()
                .collect(Collectors.joining(", "));

        int totalQuantity = orderDetails.stream()
                .mapToInt(ExportOrderDetail::getQuantity)
                .sum();

        return ExportOrderBoardResponse.builder()
                .id(Long.valueOf(first.getExportOrder().getId()))
                .exportCode(first.getExportOrder().getExportCode())
                .skuCode(allSkuCodes)
                .skuName(allSkuNames)
                .createdAt(first.getExportOrder().getCreatedAt().atStartOfDay())
                .source(first.getExportOrder().getSource().toString())
                .quantity(totalQuantity)
                .build();
    }

}
