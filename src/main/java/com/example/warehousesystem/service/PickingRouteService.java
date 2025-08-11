package com.example.warehousesystem.service;

import com.example.warehousesystem.dto.request.PickingRouteRequest;
import com.example.warehousesystem.dto.response.PickingRouteResponse;
import com.example.warehousesystem.entity.Box;
import com.example.warehousesystem.entity.SKU;
import com.example.warehousesystem.mapper.PickingRouteMapper;
import com.example.warehousesystem.repository.BoxRepository;
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

    public List<PickingRouteResponse> getOptimalPickingRoute(PickingRouteRequest request) {
        // Lấy danh sách skuCode -> quantity yêu cầu
        Map<String, Integer> requiredSkuMap = request.getSkuList().stream()
                .collect(Collectors.toMap(
                        PickingRouteRequest.SKURequest::getSkuCode,
                        PickingRouteRequest.SKURequest::getQuantity
                ));

        // Lấy ID của tất cả SKU trong yêu cầu
        List<SKU> skus = skuRepository.findBySkuCodeIn(new ArrayList<>(requiredSkuMap.keySet()));
        if (skus.isEmpty()) {
            throw new RuntimeException("Không tìm thấy SKU nào phù hợp với yêu cầu!");
        }

        Map<Integer, String> skuIdToCode = skus.stream()
                .collect(Collectors.toMap(SKU::getId, SKU::getSkuCode));

        List<Integer> skuIds = new ArrayList<>(skuIdToCode.keySet());

        // Lấy tất cả box chứa các SKU này (có usedCapacity > 0), sắp xếp theo shelf gần nhất
        List<Box> availableBoxes = boxRepository.findAvailableBoxesBySkuIds(skuIds);

        List<PickingRouteResponse> result = new ArrayList<>();

        // Theo từng box, lấy số lượng còn thiếu của SKU
        for (Box box : availableBoxes) {
            String skuCode = skuIdToCode.get(box.getSku().getId());
            int needed = requiredSkuMap.getOrDefault(skuCode, 0);

            if (needed <= 0) {
                continue; // Đã đủ hàng cho SKU này
            }

            int quantityToPick = Math.min(needed, box.getUsedCapacity());
            result.add(PickingRouteMapper.toResponse(box, quantityToPick));

            // Giảm số lượng cần cho SKU này
            requiredSkuMap.put(skuCode, needed - quantityToPick);

            // Nếu tất cả SKU đã đủ thì dừng
            boolean allPicked = requiredSkuMap.values().stream().allMatch(v -> v <= 0);
            if (allPicked) break;
        }

        return result;
    }
}
