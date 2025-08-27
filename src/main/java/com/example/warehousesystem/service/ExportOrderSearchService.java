package com.example.warehousesystem.service;

import com.example.warehousesystem.dto.request.SearchExportOrder2Request;
import com.example.warehousesystem.dto.request.SearchExportOrderRequest;
import com.example.warehousesystem.dto.response.AllExportOrderResponse;
import com.example.warehousesystem.dto.response.ExportOrderBoardResponse;
import com.example.warehousesystem.entity.ExportOrderDetail;
import com.example.warehousesystem.mapper.ExportOrderFullMapper;
import com.example.warehousesystem.repository.ExportOrderDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExportOrderSearchService {

    private final ExportOrderDetailRepository exportOrderDetailRepository;

    public List<AllExportOrderResponse> searchExportOrders(SearchExportOrderRequest request) {

        List<ExportOrderDetail> details = exportOrderDetailRepository.searchExportOrders(
                request.getExportCode(),
                request.getSkuCode(),
                request.getStartDate(),
                request.getEndDate()
        );

        return details.stream()
                .map(ExportOrderFullMapper::toAllResponse)
                .toList();
    }

    public List<ExportOrderBoardResponse> searchExportOrders2(SearchExportOrder2Request request) {

        List<ExportOrderDetail> details = exportOrderDetailRepository.searchExportOrders2(
                request.getExportCode(),
                request.getSource(),
                request.getStartDate(),
                request.getEndDate()
        );

        Map<Long, List<ExportOrderDetail>> grouped = details.stream()
                .collect(Collectors.groupingBy(d -> Long.valueOf(d.getExportOrder().getId())));

        return grouped.values().stream()
                .map(ExportOrderFullMapper::toBoardResponse)
                .toList();
    }
}
