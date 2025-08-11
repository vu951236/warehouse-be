package com.example.warehousesystem.service;

import com.example.warehousesystem.dto.request.ImportSingleItemRequest;
import com.example.warehousesystem.dto.response.ImportItemsResponse;
import com.example.warehousesystem.entity.*;
import com.example.warehousesystem.exception.AppException;
import com.example.warehousesystem.exception.ErrorCode;
import com.example.warehousesystem.mapper.ItemImportMapper;
import com.example.warehousesystem.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImportSingleItemByForm {

    private final SKURepository skuRepository;
    private final BinRepository binRepository;
    private final BoxRepository boxRepository;
    private final ItemRepository itemRepository;
    private final ImportOrderRepository importOrderRepository;
    private final ImportOrderDetailRepository importOrderDetailRepository;
    private final UserRepository userRepository;

    @Transactional
    public ImportItemsResponse importSingleItemByForm(ImportSingleItemRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        SKU sku = skuRepository.findBySkuCode(request.getSkuCode())
                .orElseThrow(() -> new AppException(ErrorCode.SKU_NOT_FOUND));

        ImportOrder importOrder = ItemImportMapper.toImportOrder(ImportOrder.Source.valueOf(request.getSource()), request.getNote(), user);
        importOrderRepository.save(importOrder);

        float requiredVolume = sku.getUnitVolume();

        // Tìm bin còn sức chứa
        List<Bin> availableBins = binRepository.findBinsWithAvailableCapacity();
        Bin targetBin = availableBins.stream()
                .filter(bin -> bin.getCapacity() - binRepository.getUsedCapacityInBin(bin.getId()) >= requiredVolume)
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.NO_BIN_CAPACITY));

        // Tìm box phù hợp
        List<Box> availableBoxes = boxRepository.findAvailableBoxes(sku.getId(), sku.getUnitVolume())
                .stream()
                .filter(box -> box.getBin().getId().equals(targetBin.getId()))
                .collect(Collectors.toList());

        Box targetBox = availableBoxes.stream()
                .filter(box -> box.getCapacity() - box.getUsedCapacity() >= sku.getUnitVolume())
                .findFirst()
                .orElseGet(() -> {
                    int boxCount = boxRepository.countBoxesInBin(targetBin.getId());
                    String newBoxCode = targetBin.getBinCode() + "-BOX-" + (boxCount + 1);
                    Box newBox = Box.builder()
                            .boxCode(newBoxCode)
                            .bin(targetBin)
                            .sku(sku)
                            .capacity(1000)
                            .usedCapacity(0)
                            .isDeleted(false)
                            .build();
                    boxRepository.save(newBox);
                    return newBox;
                });

        // Tạo 1 item duy nhất
        Item item = ItemImportMapper.toItem(targetBox, sku);
        String barcode;
        do {
            barcode = sku.getSkuCode() + "-" + UUID.randomUUID().toString().substring(0, 8);
        } while (itemRepository.existsByBarcode(barcode));

        item.setBarcode(barcode);
        itemRepository.save(item);

        // Cập nhật capacity box
        targetBox.setUsedCapacity((int) (targetBox.getUsedCapacity() + sku.getUnitVolume()));
        boxRepository.save(targetBox);

        // Lưu chi tiết đơn nhập
        ImportOrderDetail detail = ItemImportMapper.toDetail(importOrder, sku, 1);
        importOrderDetailRepository.save(detail);

        return ImportItemsResponse.builder()
                .importOrderId(importOrder.getId())
                .importedItems(List.of(new ImportItemsResponse.ImportedItemInfo(
                        barcode, item.getId(), targetBox.getId(), targetBin.getId()
                )))
                .build();
    }
}
