package com.example.warehousesystem.service;

import com.example.warehousesystem.dto.response.ExportOrderResponse;
import com.example.warehousesystem.dto.response.ExportOrderDetailResponse;
import com.example.warehousesystem.entity.ExportOrder;
import com.example.warehousesystem.entity.ExportOrderDetail;
import com.example.warehousesystem.mapper.ExportOrderMapper;
import com.example.warehousesystem.mapper.ExportOrderDetailMapper;
import com.example.warehousesystem.repository.ExportOrderRepository;
import com.example.warehousesystem.repository.ExportOrderDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
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
}
