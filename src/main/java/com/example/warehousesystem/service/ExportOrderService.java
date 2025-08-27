package com.example.warehousesystem.service;

import com.example.warehousesystem.dto.request.ExportOrderSearchRequest;
import com.example.warehousesystem.dto.response.*;
import com.example.warehousesystem.entity.ExportOrder;
import com.example.warehousesystem.entity.ExportOrderDetail;
import com.example.warehousesystem.entity.SKU;
import com.example.warehousesystem.mapper.ExportOrderFullMapper;
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
        return exportOrderDetailRepository.findAll().stream()
                .map(ExportOrderFullMapper::toAllResponse)
                .toList();
    }

    public ExportOrderFullResponse getFullExportOrderById(Integer orderId) {
        ExportOrder exportOrder = exportOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn xuất"));

        List<ExportOrderDetail> details = exportOrderDetailRepository.findByExportOrderId(orderId);
        return ExportOrderFullMapper.toFullResponse(exportOrder, details);
    }

    public ExportOrderFullResponse getFullExportOrderByDetailId(Integer detailId) {
        ExportOrderDetail detail = exportOrderDetailRepository.findById(detailId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết đơn xuất"));

        return ExportOrderFullMapper.toFullResponse(detail);
    }


    public List<ExportOrderBoardResponse> getAllExportOrderDetailsMergedWithSkuList() {
        List<ExportOrderDetail> details = exportOrderDetailRepository.findAll();

        return details.stream()
                .collect(Collectors.groupingBy(d -> d.getExportOrder().getId()))
                .values().stream()
                .map(ExportOrderFullMapper::toBoardResponse)
                .toList();
    }

}
