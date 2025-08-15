package com.example.warehousesystem.service;

import com.example.warehousesystem.dto.request.PickingRouteRequest;
import com.example.warehousesystem.dto.response.PickingRouteResponse;
import com.example.warehousesystem.entity.Box;
import com.example.warehousesystem.entity.Item;
import com.example.warehousesystem.entity.SKU;
import com.example.warehousesystem.mapper.PickingRouteMapper;
import com.example.warehousesystem.repository.BoxRepository;
import com.example.warehousesystem.repository.ItemRepository;
import com.example.warehousesystem.repository.SKURepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PickingRouteService {

    private final BoxRepository boxRepository;
    private final SKURepository skuRepository;
    private final ItemRepository itemRepository;

    public List<PickingRouteResponse> getOptimalPickingRoute(PickingRouteRequest request) {
        Map<String, Integer> requiredSkuMap = request.getSkuList().stream()
                .collect(Collectors.toMap(
                        PickingRouteRequest.SKURequest::getSkuCode,
                        PickingRouteRequest.SKURequest::getQuantity
                ));

        List<SKU> skus = skuRepository.findBySkuCodeIn(new ArrayList<>(requiredSkuMap.keySet()));
        if (skus.isEmpty()) {
            throw new RuntimeException("Không tìm thấy SKU nào phù hợp với yêu cầu!");
        }

        Map<Integer, String> skuIdToCode = skus.stream()
                .collect(Collectors.toMap(SKU::getId, SKU::getSkuCode));

        List<Integer> skuIds = new ArrayList<>(skuIdToCode.keySet());
        List<Box> availableBoxes = boxRepository.findAvailableBoxesBySkuIds(skuIds);

        List<PickingRouteResponse> result = new ArrayList<>();

        for (Box box : availableBoxes) {
            String skuCode = skuIdToCode.get(box.getSku().getId());
            int needed = requiredSkuMap.getOrDefault(skuCode, 0);

            if (needed <= 0) continue;

            int quantityToPick = Math.min(needed, box.getUsedCapacity());

            List<String> barcodes = itemRepository
                    .findByBoxIdAndStatus(box.getId(), Item.Status.available).stream()
                    .map(Item::getBarcode)
                    .limit(quantityToPick)
                    .toList();

            result.add(PickingRouteMapper.toResponse(box, quantityToPick, barcodes));

            requiredSkuMap.put(skuCode, needed - quantityToPick);

            if (requiredSkuMap.values().stream().allMatch(v -> v <= 0)) break;
        }

        return result;
    }
}
