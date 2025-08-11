package com.example.warehousesystem.service;

import com.example.warehousesystem.dto.request.SearchExportBySKURequest;
import com.example.warehousesystem.dto.response.SearchExportBySKUResponse;
import com.example.warehousesystem.entity.ExportOrder;
import com.example.warehousesystem.entity.ExportOrderDetail;
import com.example.warehousesystem.mapper.SearchExportBySKUMapper;
import com.example.warehousesystem.repository.ExportOrderDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchExportBySKUService {

    private final ExportOrderDetailRepository exportOrderDetailRepository;

    public List<SearchExportBySKUResponse> searchBySku(SearchExportBySKURequest request) {
        LocalDateTime fromDateTime = null;
        LocalDateTime toDateTime = null;

        if (request.getFromDate() != null && !request.getFromDate().isEmpty()) {
            fromDateTime = LocalDate.parse(request.getFromDate()).atStartOfDay();
        }
        if (request.getToDate() != null && !request.getToDate().isEmpty()) {
            toDateTime = LocalDate.parse(request.getToDate()).atTime(23, 59, 59);
        }

        List<Object[]> results = exportOrderDetailRepository.findExportDetailsBySku(
                request.getSkuCode(),
                fromDateTime,
                toDateTime
        );

        List<SearchExportBySKUResponse> responses = new ArrayList<>();
        for (Object[] row : results) {
            ExportOrderDetail detail = (ExportOrderDetail) row[0];
            ExportOrder order = (ExportOrder) row[1];
            String warehouseName = (String) row[2];

            responses.add(SearchExportBySKUMapper.toExportBySkuResponse(order, detail, warehouseName));
        }

        return responses;
    }
}
