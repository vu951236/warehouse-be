package com.example.warehousesystem.service;

import com.example.warehousesystem.dto.response.BoxItemResponse;
import com.example.warehousesystem.dto.response.SkuDetailResponse;
import com.example.warehousesystem.dto.response.SkuWithBoxesResponse;
import com.example.warehousesystem.entity.SKU;
import com.example.warehousesystem.mapper.SkuMapper;
import com.example.warehousesystem.repository.BoxRepository;
import com.example.warehousesystem.repository.SKURepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkuService {
    private final SKURepository skuRepository;
    private final BoxRepository boxRepository;
    private final SkuMapper skuMapper;

    public List<SkuDetailResponse> getAllSkus() {
        return skuRepository.findAll().stream()
                .map(sku -> {
                    Long itemCount = skuRepository.countItemsBySkuId(sku.getId());
                    return skuMapper.toResponse(sku, itemCount);
                })
                .collect(Collectors.toList());
    }

    public SkuWithBoxesResponse getSkuDetailById(Integer id) {
        SKU sku = skuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SKU not found with id: " + id));

        Long totalItemCount = skuRepository.countItemsBySkuId(sku.getId());

        List<BoxItemResponse> boxes = boxRepository.findBySkuAndIsDeletedFalse(sku).stream()
                .map(box -> {
                    Long itemCountInBox = boxRepository.countItemsInBox(box.getId());
                    return BoxItemResponse.builder()
                            .boxCode(box.getBoxCode())
                            .itemCount(itemCountInBox)
                            .build();
                })
                .toList();

        return SkuWithBoxesResponse.builder()
                .id(sku.getId())
                .skuCode(sku.getSkuCode())
                .name(sku.getName())
                .size(sku.getSize())
                .color(sku.getColor())
                .type(sku.getType())
                .unitVolume(sku.getUnitVolume())
                .totalItemCount(totalItemCount)
                .boxes(boxes)
                .build();
    }

}
