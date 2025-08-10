package com.example.warehousesystem.service;

import com.example.warehousesystem.dto.request.UrgentOrderRequest;
import com.example.warehousesystem.dto.response.UrgentOrderResponse;
import com.example.warehousesystem.entity.ExportOrder;
import com.example.warehousesystem.mapper.UrgentOrderMapper;
import com.example.warehousesystem.repository.ExportOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UrgentOrderService {

    private final ExportOrderRepository exportOrderRepository;

    public List<UrgentOrderResponse> getUrgentOrders(UrgentOrderRequest request) {
        int limit = (request.getMaxResults() != null && request.getMaxResults() > 0)
                ? request.getMaxResults()
                : 10;

        // Lấy tất cả đơn hàng đang chờ xuất (hoặc trạng thái phù hợp)
        List<ExportOrder> orders = exportOrderRepository.findAll();

        // Lọc & sắp xếp: urgent = true trước, sau đó theo ngày tạo ASC
        List<ExportOrder> sortedOrders = orders.stream()
                .filter(order -> order.getStatus() == ExportOrder.Status.confirmed)
                .sorted(Comparator
                        .comparing((ExportOrder o) -> !o.getUrgent()) // urgent = true đứng trước
                        .thenComparing(ExportOrder::getCreatedAt))
                .limit(limit)
                .collect(Collectors.toList());

        return sortedOrders.stream()
                .map(UrgentOrderMapper::toResponse)
                .collect(Collectors.toList());
    }
}
