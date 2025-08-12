package com.example.warehousesystem.service;

import com.example.warehousesystem.dto.request.SearchImportBySKURequest;
import com.example.warehousesystem.dto.response.ImportOrderDetailResponse;
import com.example.warehousesystem.dto.response.SearchImportBySKUResponse;
import com.example.warehousesystem.entity.ImportOrder;
import com.example.warehousesystem.entity.ImportOrderDetail;
import com.example.warehousesystem.mapper.SearchImportBySKUMapper;
import com.example.warehousesystem.repository.ImportOrderDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImportOrderDetailService {

    private final ImportOrderDetailRepository importOrderDetailRepository;

    public List<SearchImportBySKUResponse> getImportHistoryBySku(SearchImportBySKURequest request) {
        LocalDateTime fromDate = request.getFromDate() != null
                ? LocalDate.parse(request.getFromDate()).atStartOfDay()
                : null;
        LocalDateTime toDate = request.getToDate() != null
                ? LocalDate.parse(request.getToDate()).atTime(23, 59, 59)
                : null;

        List<Object[]> rawResults = importOrderDetailRepository.findImportDetailsBySku(
                request.getSkuCode(),
                fromDate,
                toDate
        );

        return rawResults.stream()
                .map(row -> {
                    ImportOrderDetail detail = (ImportOrderDetail) row[0];
                    ImportOrder order = (ImportOrder) row[1];
                    String warehouseName = (String) row[2];

                    return SearchImportBySKUMapper.toImportBySkuResponse(order, detail, warehouseName);
                })
                .toList();
    }

    public List<ImportOrderDetailResponse> getImportOrderDetails(Integer importOrderId) {
        List<ImportOrderDetail> details = importOrderDetailRepository.findByImportOrderId(importOrderId);

        return details.stream()
                .map(detail -> ImportOrderDetailResponse.builder()
                        .skuId(detail.getSku().getId())
                        .skuCode(detail.getSku().getSkuCode())
                        .skuName(detail.getSku().getName())
                        .quantity(detail.getQuantity())
                        .build())
                .collect(Collectors.toList());
    }
}
