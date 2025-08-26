package com.example.warehousesystem.service;

import com.example.warehousesystem.dto.request.ExportOrderSearchRequest;
import com.example.warehousesystem.dto.response.*;
import com.example.warehousesystem.entity.ExportOrder;
import com.example.warehousesystem.entity.ExportOrderDetail;
import com.example.warehousesystem.entity.SKU;
import com.example.warehousesystem.mapper.ExportOrderMapper;
import com.example.warehousesystem.mapper.ExportOrderDetailMapper;
import com.example.warehousesystem.repository.ExportOrderRepository;
import com.example.warehousesystem.repository.ExportOrderDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExportOrderService {

    private final ExportOrderRepository exportOrderRepository;
    private final ExportOrderDetailRepository exportOrderDetailRepository;
    private final ExportOrderMapper exportOrderMapper;

    // Lấy tất cả đơn xuất
    public List<ExportOrderResponse> getAllExportOrders() {
        List<ExportOrder> exportOrders = exportOrderRepository.findAll();
        return exportOrders.stream()
                .map(exportOrderMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Lấy chi tiết đơn xuất theo orderId
    public List<ExportOrderDetailResponse> getExportOrderDetailsByOrderId(Integer orderId) {
        List<ExportOrderDetail> details = exportOrderDetailRepository.findByExportOrderId(orderId);
        return details.stream()
                .map(ExportOrderDetailMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Tìm kiếm đơn xuất kho theo tiêu chí
     */
    public List<ExportOrderResponse> searchExportOrders(ExportOrderSearchRequest request) {
        // Convert LocalDate → LocalDateTime cho query
        LocalDateTime startDateTime = request.getStartDate() != null
                ? request.getStartDate().atStartOfDay()
                : null;

        LocalDateTime endDateTime = request.getEndDate() != null
                ? request.getEndDate().atTime(LocalTime.MAX)
                : null;

        List<ExportOrder> orders = exportOrderRepository.searchExportOrders(
                request.getSource(),
                request.getStatus(),
                request.getCreatedBy(),
                startDateTime,
                endDateTime
        );

        return orders.stream()
                .map(exportOrderMapper::toResponse)
                .toList();
    }

    public List<AllExportOrderResponse> getAllExportOrderDetails() {
        List<ExportOrderDetail> details = exportOrderDetailRepository.findAll();

        return details.stream().map(detail -> AllExportOrderResponse.builder()
                .id(detail.getId())
                .exportCode(detail.getExportOrder().getExportCode())
                .skuCode(detail.getSku().getSkuCode())
                .productName(detail.getSku().getName())
                .exportDate(detail.getExportOrder().getCreatedAt().atStartOfDay())
                .quantity(detail.getQuantity())
                .build()
        ).toList();
    }

    public ExportOrderFullResponse getFullExportOrderById(Integer orderId) {
        ExportOrder exportOrder = exportOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn xuất"));

        List<ExportOrderDetail> details = exportOrderDetailRepository.findByExportOrderId(orderId);

        return ExportOrderFullResponse.builder()
                .id(exportOrder.getId())
                .exportCode(exportOrder.getExportCode())
                .source(exportOrder.getSource().toString())
                .status(exportOrder.getStatus().toString())
                .createdBy(exportOrder.getCreatedBy().getUsername())
                .createdAt(exportOrder.getCreatedAt().atStartOfDay())
                .note(exportOrder.getNote())
                .details(details.stream().map(d -> {
                    SKU sku = d.getSku();
                    return ExportOrderFullResponse.ExportOrderDetailItem.builder()
                            .id(d.getId())
                            .skuCode(sku.getSkuCode())
                            .skuName(sku.getName())
                            .size(sku.getSize())
                            .color(sku.getColor())
                            .type(sku.getType())
                            .unitVolume(Double.valueOf(sku.getUnitVolume()))
                            .quantity(d.getQuantity())
                            .build();
                }).collect(Collectors.toList()))
                .build();
    }

    public ExportOrderFullResponse getFullExportOrderByDetailId(Integer detailId) {
        ExportOrderDetail detail = exportOrderDetailRepository.findById(detailId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết đơn xuất"));

        ExportOrder exportOrder = detail.getExportOrder();
        SKU sku = detail.getSku();

        return ExportOrderFullResponse.builder()
                .id(exportOrder.getId())
                .exportCode(exportOrder.getExportCode())
                .source(exportOrder.getSource().toString())
                .status(exportOrder.getStatus().toString())
                .createdBy(exportOrder.getCreatedBy().getUsername())
                .createdAt(exportOrder.getCreatedAt().atStartOfDay())
                .note(exportOrder.getNote())
                .details(Collections.singletonList(
                        ExportOrderFullResponse.ExportOrderDetailItem.builder()
                                .id(detail.getId())
                                .skuCode(sku.getSkuCode())
                                .skuName(sku.getName())
                                .size(sku.getSize())
                                .color(sku.getColor())
                                .type(sku.getType())
                                .unitVolume(Double.valueOf(sku.getUnitVolume()))
                                .quantity(detail.getQuantity())
                                .build()
                ))
                .build();
    }

    public List<ExportOrderBoardResponse> getAllExportOrderDetailsMergedWithSkuList() {
        List<ExportOrderDetail> details = exportOrderDetailRepository.findAll();

        if (details.isEmpty()) {
            return Collections.emptyList();
        }

        // Gom nhóm theo exportOrderId
        Map<Long, List<ExportOrderDetail>> groupedByOrder = details.stream()
                .collect(Collectors.groupingBy(d -> Long.valueOf(d.getExportOrder().getId())));

        List<ExportOrderBoardResponse> result = new ArrayList<>();

        for (Map.Entry<Long, List<ExportOrderDetail>> entry : groupedByOrder.entrySet()) {
            List<ExportOrderDetail> orderDetails = entry.getValue();
            ExportOrderDetail first = orderDetails.get(0);

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

            ExportOrderBoardResponse merged = ExportOrderBoardResponse.builder()
                    .id(Long.valueOf(first.getExportOrder().getId()))
                    .exportCode(first.getExportOrder().getExportCode())
                    .skuCode(allSkuCodes)
                    .skuName(allSkuNames)
                    .createdAt(first.getExportOrder().getCreatedAt().atStartOfDay())
                    .quantity(totalQuantity)
                    .build();

            result.add(merged);
        }

        return result;
    }

}
