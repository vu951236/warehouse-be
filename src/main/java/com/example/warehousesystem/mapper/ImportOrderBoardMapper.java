package com.example.warehousesystem.mapper;

import com.example.warehousesystem.dto.response.ImportOrderBoardResponse;
import com.example.warehousesystem.entity.ImportOrderDetail;

import java.util.*;
import java.util.stream.Collectors;

public class ImportOrderBoardMapper {

    // Map đơn lẻ (1 detail -> 1 response)
    public static ImportOrderBoardResponse toResponse(ImportOrderDetail detail) {
        return ImportOrderBoardResponse.builder()
                .id(Long.valueOf(detail.getImportOrder().getId()))
                .importCode(detail.getImportOrder().getImportCode())
                .skuCode(detail.getSku().getSkuCode())
                .skuName(detail.getSku().getName())
                .createdAt(detail.getImportOrder().getCreatedAt().atStartOfDay())
                .quantity(detail.getQuantity())
                .size(detail.getSku().getSize())
                .color(detail.getSku().getColor())
                .type(detail.getSku().getType())
                .unitVolume(detail.getSku().getUnitVolume())
                .source(detail.getImportOrder().getSource().toString())
                .note(detail.getImportOrder().getNote())
                .build();
    }

    // Map nhiều detail, gom nhóm theo ImportOrderId
    public static List<ImportOrderBoardResponse> toBoardResponses(List<ImportOrderDetail> details) {
        if (details == null || details.isEmpty()) {
            return Collections.emptyList();
        }

        // Gom nhóm theo importOrderId
        Map<Long, List<ImportOrderDetail>> groupedByOrder = details.stream()
                .collect(Collectors.groupingBy(d -> Long.valueOf(d.getImportOrder().getId())));

        List<ImportOrderBoardResponse> result = new ArrayList<>();

        for (Map.Entry<Long, List<ImportOrderDetail>> entry : groupedByOrder.entrySet()) {
            List<ImportOrderDetail> orderDetails = entry.getValue();
            ImportOrderDetail first = orderDetails.get(0);

            String allSkuCodes = orderDetails.stream()
                    .map(d -> d.getSku().getSkuCode())
                    .distinct()
                    .collect(Collectors.joining(", "));

            String allSkuNames = orderDetails.stream()
                    .map(d -> d.getSku().getName())
                    .distinct()
                    .collect(Collectors.joining(", "));

            int totalQuantity = orderDetails.stream()
                    .mapToInt(ImportOrderDetail::getQuantity)
                    .sum();

            ImportOrderBoardResponse merged = ImportOrderBoardResponse.builder()
                    .id(Long.valueOf(first.getImportOrder().getId()))
                    .importCode(first.getImportOrder().getImportCode())
                    .skuCode(allSkuCodes)
                    .skuName(allSkuNames)
                    .createdAt(first.getImportOrder().getCreatedAt().atStartOfDay())
                    .quantity(totalQuantity)
                    .source(first.getImportOrder().getSource().toString())
                    .note(first.getImportOrder().getNote())
                    .build();

            result.add(merged);
        }

        return result;
    }
}
