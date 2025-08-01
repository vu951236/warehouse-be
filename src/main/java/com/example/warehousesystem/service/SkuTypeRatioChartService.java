package com.example.warehousesystem.service;

import com.example.warehousesystem.dto.response.SkuTypeRatioChartResponse;
import com.example.warehousesystem.mapper.SkuTypeRatioChartMapper;
import com.example.warehousesystem.repository.SKURepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkuTypeRatioChartService {

    private final SKURepository skuRepository;

    public List<SkuTypeRatioChartResponse> getSkuTypeRatioChart() {
        List<Object[]> rawData = skuRepository.getCurrentStockRatioChart();

        return rawData.stream()
                .map(row -> {
                    String skuName = (String) row[0];
                    Long quantity = row[1] != null ? ((Number) row[1]).longValue() : 0L;
                    return SkuTypeRatioChartMapper.toResponse(skuName, quantity);
                })
                .collect(Collectors.toList());
    }
}
