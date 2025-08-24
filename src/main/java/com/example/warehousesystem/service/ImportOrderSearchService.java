package com.example.warehousesystem.service;

import com.example.warehousesystem.dto.request.ImportOrderSearchRequest;
import com.example.warehousesystem.dto.response.ImportOrderBoardResponse;
import com.example.warehousesystem.entity.ImportOrderDetail;
import com.example.warehousesystem.repository.ImportOrderDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImportOrderSearchService {

    private final ImportOrderDetailRepository importOrderDetailRepository;


    public List<ImportOrderBoardResponse> searchImportOrders(ImportOrderSearchRequest request) {
        List<ImportOrderDetail> details = importOrderDetailRepository.searchImportOrdersV2(
                request.getImportCode(),
                request.getSkuCode(),
                request.getCreatedAt()
        );


        return details.stream()
                .map(d -> ImportOrderBoardResponse.builder()
                        .importCode(d.getImportOrder().getImportCode())
                        .skuCode(d.getSku().getSkuCode())
                        .skuName(d.getSku().getName())
                        .createdAt(d.getImportOrder().getCreatedAt()) // giữ nguyên LocalDateTime
                        .build())
                .collect(Collectors.toList());
    }
}
