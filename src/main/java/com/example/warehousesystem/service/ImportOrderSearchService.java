package com.example.warehousesystem.service;

import com.example.warehousesystem.dto.request.ImportOrderSearch2Request;
import com.example.warehousesystem.dto.request.ImportOrderSearchRequest;
import com.example.warehousesystem.dto.response.ImportOrderBoardResponse;
import com.example.warehousesystem.mapper.ImportOrderBoardMapper;
import com.example.warehousesystem.repository.ImportOrderDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ImportOrderSearchService {

    private final ImportOrderDetailRepository importOrderDetailRepository;

    public List<ImportOrderBoardResponse> searchImportOrders(ImportOrderSearchRequest request) {
        return importOrderDetailRepository.searchImportOrdersV2(
                        request.getImportCode(),
                        request.getSkuCode(),
                        request.getStartDate(),
                        request.getEndDate()
                ).stream()
                .map(ImportOrderBoardMapper::toResponse)
                .toList();
    }

    public List<ImportOrderBoardResponse> searchImportOrdersMerged(ImportOrderSearch2Request request) {
        return ImportOrderBoardMapper.toBoardResponses(
                importOrderDetailRepository.searchImportOrdersMerged(
                        request.getImportCode(),
                        request.getSource(),
                        request.getStartDate(),
                        request.getEndDate()
                )
        );
    }

}
