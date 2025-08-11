package com.example.warehousesystem.service;

import com.example.warehousesystem.dto.response.ImportOrderBoardResponse;
import com.example.warehousesystem.dto.response.ImportOrderResponse;
import com.example.warehousesystem.dto.response.ImportOrderDetailResponse;
import com.example.warehousesystem.entity.ImportOrder;
import com.example.warehousesystem.entity.ImportOrderDetail;
import com.example.warehousesystem.mapper.ImportOrderBoardMapper;
import com.example.warehousesystem.mapper.ImportOrderMapper;
import com.example.warehousesystem.mapper.ImportOrderDetailMapper;
import com.example.warehousesystem.repository.ImportOrderRepository;
import com.example.warehousesystem.repository.ImportOrderDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImportOrderService {

    private final ImportOrderRepository importOrderRepository;
    private final ImportOrderDetailRepository importOrderDetailRepository;
    private final ImportOrderMapper importOrderMapper;

    // Lấy tất cả đơn nhập
    public List<ImportOrderResponse> getAllImportOrders() {
        List<ImportOrder> importOrders = importOrderRepository.findAll();
        return importOrders.stream()
                .map(importOrderMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Lấy chi tiết đơn nhập theo id đơn nhập
    public List<ImportOrderDetailResponse> getImportOrderDetailsByOrderId(Integer orderId) {
        List<ImportOrderDetail> details = importOrderDetailRepository.findByImportOrderId(orderId);
        return details.stream()
                .map(ImportOrderDetailMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Lấy tất cả đơn nhập cho bảng
    public List<ImportOrderBoardResponse> getAllImportOrderDetails() {
        List<ImportOrderDetail> details = importOrderDetailRepository.findAll();
        return details.stream()
                .map(ImportOrderBoardMapper::toResponse)
                .collect(Collectors.toList());
    }


}
