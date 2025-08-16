package com.example.warehousesystem.service;

import com.example.warehousesystem.dto.response.SkuDetailResponse;
import com.example.warehousesystem.entity.SKU;
import com.example.warehousesystem.repository.SKURepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkuService {
    private final SKURepository skuRepository;

    public List<SkuDetailResponse> getAllSkus() {
        List<SKU> skus = skuRepository.findAll();

        return skus.stream().map(sku -> {
            Long itemCount = skuRepository.countItemsBySkuId(sku.getId());
            return SkuDetailResponse.builder()
                    .skuCode(sku.getSkuCode())
                    .itemCount(itemCount)
                    .name(sku.getName())
                    .size(sku.getSize())
                    .color(sku.getColor())
                    .type(sku.getType())
                    .unitVolume(Double.valueOf(sku.getUnitVolume()))
                    .build();
        }).collect(Collectors.toList());
    }
}
