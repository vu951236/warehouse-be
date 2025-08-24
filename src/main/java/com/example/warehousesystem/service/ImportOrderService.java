package com.example.warehousesystem.service;

import com.example.warehousesystem.dto.request.ImportOrderRequest;
import com.example.warehousesystem.dto.response.ImportOrderBoardResponse;
import com.example.warehousesystem.dto.response.ImportOrderFullResponse;
import com.example.warehousesystem.dto.response.ImportOrderResponse;
import com.example.warehousesystem.dto.response.ImportOrderDetailResponse;
import com.example.warehousesystem.entity.*;
import com.example.warehousesystem.mapper.ImportOrderBoardMapper;
import com.example.warehousesystem.mapper.ImportOrderMapper;
import com.example.warehousesystem.mapper.ImportOrderDetailMapper;
import com.example.warehousesystem.mapper.ItemImportMapper;
import com.example.warehousesystem.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImportOrderService {

    private final ImportOrderRepository importOrderRepository;
    private final ImportOrderDetailRepository importOrderDetailRepository;
    private final SKURepository skuRepository;
    private final UserRepository userRepository;
    private final BoxRepository boxRepository;
    private final BinRepository binRepository;
    private final ItemRepository itemRepository;
    private final ImportOrderMapper importOrderMapper;

    // Lấy tất cả đơn nhập
    public List<ImportOrderResponse> getAllImportOrders() {
        List<ImportOrder> importOrders = importOrderRepository.findAll();
        return importOrders.stream()
                .map(importOrderMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Lấy chi tiết đơn nhập theo id đơn nhập
    public List<ImportOrderDetailResponse> getImportOrderDetailsByOrderId(Integer orderId) {
        List<ImportOrderDetail> details = importOrderDetailRepository.findByImportOrderId(orderId);
        return details.stream()
                .map(ImportOrderDetailMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Lấy tất cả đơn nhập cho bảng
    public List<ImportOrderBoardResponse> getAllImportOrderDetails() {
        List<ImportOrderDetail> details = importOrderDetailRepository.findAll();
        return details.stream()
                .map(ImportOrderBoardMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<ImportOrderBoardResponse> getAllImportOrderDetailsMergedWithSkuList() {
        List<ImportOrderDetail> details = importOrderDetailRepository.findAll();

        if (details.isEmpty()) {
            return Collections.emptyList();
        }

        // Gom nhóm theo importOrderId
        Map<Long, List<ImportOrderDetail>> groupedByOrder = details.stream()
                .collect(Collectors.groupingBy(d -> Long.valueOf(d.getImportOrder().getId())));

        List<ImportOrderBoardResponse> result = new ArrayList<>();

        for (Map.Entry<Long, List<ImportOrderDetail>> entry : groupedByOrder.entrySet()) {
            List<ImportOrderDetail> orderDetails = entry.getValue();
            ImportOrderDetail first = orderDetails.get(0);

            String allSkuCodes = orderDetails.stream()
                    .map(d -> d.getSku().getSkuCode())
                    .distinct()
                    .collect(Collectors.joining(", "));

            String allSkuNames = orderDetails.stream()
                    .map(d -> d.getSku().getName())
                    .distinct()
                    .collect(Collectors.joining(", "));

            int totalQuantity = orderDetails.stream()
                    .mapToInt(ImportOrderDetail::getQuantity)
                    .sum();

            ImportOrderBoardResponse merged = ImportOrderBoardResponse.builder()
                    .id(Long.valueOf(first.getImportOrder().getId()))
                    .importCode(first.getImportOrder().getImportCode())
                    .skuCode(allSkuCodes)
                    .skuName(allSkuNames)
                    .createdAt(first.getImportOrder().getCreatedAt())
                    .quantity(totalQuantity)
                    .build();

            result.add(merged);
        }

        return result;
    }

    public ImportOrderFullResponse getFullImportOrderById(Integer orderId) {
        ImportOrder importOrder = importOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn nhập"));

        List<ImportOrderDetail> details = importOrderDetailRepository.findByImportOrderId(orderId);

        return ImportOrderFullResponse.builder()
                .id(importOrder.getId())
                .importCode(importOrder.getImportCode())
                .source(importOrder.getSource().toString())
                .status(importOrder.getStatus().toString())
                .createdBy(importOrder.getCreatedBy().getUsername())
                .createdAt(importOrder.getCreatedAt())
                .note(importOrder.getNote())
                .details(details.stream().map(d -> {
                    SKU sku = d.getSku();
                    return ImportOrderFullResponse.ImportOrderDetailItem.builder()
                            .id(d.getId())
                            .skuCode(sku.getSkuCode())
                            .skuName(sku.getName())
                            .size(sku.getSize())
                            .color(sku.getColor())
                            .type(sku.getType())
                            .unitVolume(sku.getUnitVolume())
                            .quantity(d.getQuantity())
                            .build();
                }).collect(Collectors.toList()))
                .build();
    }

    public ImportOrderFullResponse getFullImportOrderByDetailId(Integer detailId) {
        ImportOrderDetail detail = importOrderDetailRepository.findById(detailId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết đơn nhập"));

        ImportOrder importOrder = detail.getImportOrder();
        SKU sku = detail.getSku();

        return ImportOrderFullResponse.builder()
                .id(importOrder.getId())
                .importCode(importOrder.getImportCode())
                .source(importOrder.getSource().toString())
                .status(importOrder.getStatus().toString())
                .createdBy(importOrder.getCreatedBy().getUsername())
                .createdAt(importOrder.getCreatedAt())
                .note(importOrder.getNote())
                .details(Collections.singletonList(
                        ImportOrderFullResponse.ImportOrderDetailItem.builder()
                                .id(detail.getId())
                                .skuCode(sku.getSkuCode())
                                .skuName(sku.getName())
                                .size(sku.getSize())
                                .color(sku.getColor())
                                .type(sku.getType())
                                .unitVolume(sku.getUnitVolume())
                                .quantity(detail.getQuantity())
                                .build()
                ))
                .build();
    }

    @Transactional
    public ImportOrderFullResponse createImportOrder(ImportOrderRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User creator = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người tạo"));

        ImportOrder importOrder = ItemImportMapper.toImportOrder(
                ImportOrder.Source.valueOf(request.getSource()),
                request.getNote(),
                creator
        );
        ImportOrder savedOrder = importOrderRepository.save(importOrder);

        List<ImportOrderDetail> details = new ArrayList<>();

        for (ImportOrderRequest.ImportOrderDetailRequest  d : request.getDetails()) {
            SKU sku = skuRepository.findBySkuCode(d.getSkuCode())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy SKU code=" + d.getSkuCode()));

            int requiredVolume = (int) (d.getQuantity() * sku.getUnitVolume());
            List<Bin> availableBins = binRepository.findBinsWithAvailableCapacity();

            Bin targetBin = null;
            for (Bin bin : availableBins) {
                int usedCapacity = binRepository.getUsedCapacityInBin(bin.getId());
                if (bin.getCapacity() - usedCapacity >= requiredVolume) {
                    targetBin = bin;
                    break;
                }
            }
            if (targetBin == null) {
                throw new RuntimeException("Không còn bin đủ dung lượng cho SKU " + sku.getSkuCode());
            }

            Bin finalTargetBin = targetBin;
            List<Box> availableBoxes = boxRepository.findAvailableBoxes(sku.getId(), sku.getUnitVolume())
                    .stream()
                    .filter(box -> box.getBin().getId().equals(finalTargetBin.getId()))
                    .collect(Collectors.toList());

            int quantityRemaining = d.getQuantity();

            while (quantityRemaining > 0) {
                Box targetBox = null;
                for (Box box : availableBoxes) {
                    int boxFree = box.getCapacity() - box.getUsedCapacity();
                    if (boxFree >= sku.getUnitVolume()) {
                        targetBox = box;
                        break;
                    }
                }

                if (targetBox == null) {
                    int boxCount = boxRepository.countBoxesInBin(targetBin.getId());
                    String newBoxCode = targetBin.getBinCode() + "-BX-" + (boxCount + 1);

                    targetBox = Box.builder()
                            .boxCode(newBoxCode)
                            .bin(targetBin)
                            .sku(sku)
                            .capacity(100)
                            .usedCapacity(0)
                            .isDeleted(false)
                            .build();
                    boxRepository.save(targetBox);
                    availableBoxes.add(targetBox);
                }

                int boxFree = targetBox.getCapacity() - targetBox.getUsedCapacity();
                int canAddQuantity = (int) Math.min(quantityRemaining, boxFree / sku.getUnitVolume());

                for (int i = 0; i < canAddQuantity; i++) {
                    Item item = ItemImportMapper.toItem(targetBox, sku);

                    String barcode;
                    do {
                        barcode = sku.getSkuCode() + "-" + UUID.randomUUID().toString().substring(0, 8);
                    } while (itemRepository.existsByBarcode(barcode));

                    item.setBarcode(barcode);
                    itemRepository.save(item);
                }

                targetBox.setUsedCapacity((int) (targetBox.getUsedCapacity() + canAddQuantity * sku.getUnitVolume()));
                boxRepository.save(targetBox);

                quantityRemaining -= canAddQuantity;
            }

            ImportOrderDetail detail = ItemImportMapper.toDetail(savedOrder, sku, d.getQuantity());
            importOrderDetailRepository.save(detail);
            details.add(detail);
        }

        return getFullImportOrderById(savedOrder.getId());
    }

}
