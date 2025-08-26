package com.example.warehousesystem.service;

import com.example.warehousesystem.dto.ExcelItemDTO;
import com.example.warehousesystem.dto.response.ImportItemsResponse;
import com.example.warehousesystem.entity.*;
import com.example.warehousesystem.exception.AppException;
import com.example.warehousesystem.exception.ErrorCode;
import com.example.warehousesystem.mapper.ItemImportMapper;
import com.example.warehousesystem.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImportFromTempService {

    private final TempImportExcelRepository tempImportExcelRepository;
    private final SKURepository skuRepository;
    private final BinRepository binRepository;
    private final BoxRepository boxRepository;
    private final ItemRepository itemRepository;
    private final ImportOrderRepository importOrderRepository;
    private final ImportOrderDetailRepository importOrderDetailRepository;
    private final UserRepository userRepository;

    @Transactional
    public ImportItemsResponse importSelected(List<Long> tempIds) {
        List<TempImportExcel> tempItems = tempImportExcelRepository.findAllById(tempIds);
        if (tempItems.isEmpty()) throw new AppException(ErrorCode.INVALID_REQUEST);

        Long userId = tempItems.get(0).getUserId();
        User user = userRepository.findById(Math.toIntExact(userId))
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        String importCode = tempItems.get(0).getImportCode();
        String source = tempItems.get(0).getSource();
        String note = tempItems.get(0).getNote();

        ImportOrder importOrder = ImportOrder.builder()
                .importCode(importCode)
                .source(ImportOrder.Source.valueOf(source))
                .status(ImportOrder.Status.confirmed)
                .createdBy(user)
                .createdAt(LocalDate.now())
                .note(note)
                .build();
        importOrderRepository.save(importOrder);

        List<ImportItemsResponse.ImportedItemInfo> importedItems = new ArrayList<>();

        for (TempImportExcel temp : tempItems) {
            SKU sku = skuRepository.findBySkuCode(temp.getSkuCode())
                    .orElseThrow(() -> new AppException(ErrorCode.SKU_NOT_FOUND));

            int requiredVolume = (int) (temp.getQuantity() * sku.getUnitVolume());
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
                throw new AppException(ErrorCode.NO_BIN_CAPACITY);
            }

            Bin finalTargetBin = targetBin;
            List<Box> availableBoxes = boxRepository.findAvailableBoxes(sku.getId(), sku.getUnitVolume())
                    .stream()
                    .filter(box -> box.getBin().getId().equals(finalTargetBin.getId()))
                    .collect(Collectors.toList());

            int quantityRemaining = temp.getQuantity();

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

                    importedItems.add(new ImportItemsResponse.ImportedItemInfo(
                            barcode, item.getId(), targetBox.getId(), targetBin.getId()
                    ));
                }

                targetBox.setUsedCapacity((int) (targetBox.getUsedCapacity() + canAddQuantity * sku.getUnitVolume()));
                boxRepository.save(targetBox);

                quantityRemaining -= canAddQuantity;
            }

            ImportOrderDetail detail = ItemImportMapper.toDetail(importOrder, sku, temp.getQuantity());
            importOrderDetailRepository.save(detail);
        }

        // Xóa dữ liệu tạm sau khi nhập
        tempImportExcelRepository.deleteAllById(tempIds);

        return ImportItemsResponse.builder()
                .importOrderId(importOrder.getId())
                .importedItems(importedItems)
                .build();
    }
}
