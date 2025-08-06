package com.example.warehousesystem.service;

import com.example.warehousesystem.dto.request.ImportOrderSearchRequest;
import com.example.warehousesystem.dto.response.ImportOrderResponse;
import com.example.warehousesystem.entity.ImportOrder;
import com.example.warehousesystem.mapper.ImportOrderMapper;
import com.example.warehousesystem.repository.ImportOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImportOrderSearchService {

    private final ImportOrderRepository importOrderRepository;
    private final ImportOrderMapper importOrderMapper;

    public List<ImportOrderResponse> searchImportOrders(ImportOrderSearchRequest request) {
        // Xử lý date từ LocalDate → LocalDateTime
        LocalDateTime start = request.getStartDate() != null ? request.getStartDate().atStartOfDay() : null;
        LocalDateTime end = request.getEndDate() != null ? request.getEndDate().atTime(23, 59, 59) : null;

        List<ImportOrder> results = importOrderRepository.searchImportOrders(
                request.getSource(),
                request.getStatus(),
                request.getCreatedBy(),
                start,
                end
        );

        return results.stream()
                .map(importOrderMapper::toResponse)
                .toList();
    }
}
