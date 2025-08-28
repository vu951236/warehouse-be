package com.example.warehousesystem.service;

import com.example.warehousesystem.dto.request.SearchSkuRequest;
import com.example.warehousesystem.dto.response.*;
import com.example.warehousesystem.entity.Item;
import com.example.warehousesystem.entity.SKU;
import com.example.warehousesystem.mapper.SKUDamagedMapper;
import com.example.warehousesystem.mapper.SkuMapper;
import com.example.warehousesystem.repository.BoxRepository;
import com.example.warehousesystem.repository.ItemRepository;
import com.example.warehousesystem.repository.SKURepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkuService {
    private final SKURepository skuRepository;
    private final BoxRepository boxRepository;
    private final ItemRepository itemRepository;
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

    public List<SKUDamagedResponse> getAllSKUDamaged() {
        List<SKU> skus = skuRepository.findAll();

        List<SKUDamagedResponse> responses = new ArrayList<>();
        for (SKU sku : skus) {
            Long damagedCount = itemRepository.countDamagedItemsBySKU(sku);
            responses.add(SKUDamagedMapper.toResponse(sku, damagedCount));
        }

        return responses;
    }

    public SkuDamagedDetailResponse getDamagedSkuDetail(Integer skuId) {
        SKU sku = skuRepository.findById(skuId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy SKU với id: " + skuId));

        // Lấy danh sách item lỗi của SKU
        List<Item> damagedItems = itemRepository.findBySkuAndStatus(sku, Item.Status.damaged);

        Long damagedCount = (long) damagedItems.size();

        // Xác định vị trí lưu trữ (lấy distinct boxCode)
        String storageLocation = damagedItems.stream()
                .map(i -> i.getBox() != null ? i.getBox().getBoxCode() : null)
                .filter(code -> code != null)
                .distinct()
                .collect(Collectors.joining(", "));

        List<SkuDamagedDetailResponse.DamagedItemDetail> damagedItemDetails = damagedItems.stream()
                .map(i -> SkuDamagedDetailResponse.DamagedItemDetail.builder()
                        .barcode(i.getBarcode())
                        .note(i.getNote())
                        .boxCode(i.getBox() != null ? i.getBox().getBoxCode() : null)
                        .transferredAt(i.getTransferredAt())
                        .build()
                )
                .toList();

        return SkuDamagedDetailResponse.builder()
                .id(sku.getId())
                .skuCode(sku.getSkuCode())
                .name(sku.getName())
                .size(sku.getSize())
                .color(sku.getColor())
                .type(sku.getType())
                .unitVolume(sku.getUnitVolume())
                .createdAt(sku.getCreatedAt().atStartOfDay())
                .damagedItemCount(damagedCount)
                .storageLocation(storageLocation)
                .damagedItems(damagedItemDetails)
                .build();
    }

    public List<SkuDetailResponse> searchSkus(SearchSkuRequest request) {
        List<SKU> skus = skuRepository.searchSkus(
                request.getSkuCode(),
                request.getSize(),
                request.getColor(),
                request.getType(),
                request.getMinUnitVolume(),
                request.getMaxUnitVolume()
        );

        return skus.stream()
                .map(sku -> {
                    Long itemCount = skuRepository.countItemsBySkuId(sku.getId());
                    return skuMapper.toResponse(sku, itemCount);
                })
                .collect(Collectors.toList());
    }

    public List<SKUDamagedResponse> searchDamagedSkus(SearchSkuRequest request) {
        List<SKU> skus = itemRepository.searchDamagedSkus(
                request.getSkuCode(),
                request.getSize(),
                request.getColor(),
                request.getType(),
                request.getMinUnitVolume(),
                request.getMaxUnitVolume()
        );

        return skus.stream()
                .map(sku -> {
                    Long damagedCount = itemRepository.countDamagedItemsBySKU(sku);
                    return SKUDamagedMapper.toResponse(sku, damagedCount);
                })
                .collect(Collectors.toList());
    }



}
