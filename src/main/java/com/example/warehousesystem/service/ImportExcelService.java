package com.example.warehousesystem.service;

import com.example.warehousesystem.dto.ExcelItemDTO;
import com.example.warehousesystem.dto.request.ImportExcelItemRequest;
import com.example.warehousesystem.dto.response.ImportItemsResponse;
import com.example.warehousesystem.entity.*;
import com.example.warehousesystem.exception.AppException;
import com.example.warehousesystem.exception.ErrorCode;
import com.example.warehousesystem.mapper.ItemImportMapper;
import com.example.warehousesystem.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImportExcelService {

    private final SKURepository skuRepository;
    private final BinRepository binRepository;
    private final BoxRepository boxRepository;
    private final ItemRepository itemRepository;
    private final ImportOrderRepository importOrderRepository;
    private final ImportOrderDetailRepository importOrderDetailRepository;
    private final UserRepository userRepository;

    @Transactional
    public ImportItemsResponse importFromExcel(ImportExcelItemRequest request) {
        // Lấy user
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Tạo đơn nhập kho
        ImportOrder importOrder = ItemImportMapper.toImportOrder(request.getSource(), request.getNote(), user);
        importOrder.setCreatedAt(LocalDateTime.now());
        importOrderRepository.save(importOrder);

        List<ImportItemsResponse.ImportedItemInfo> importedItems = new ArrayList<>();

        // Xử lý từng item trong Excel
        for (ExcelItemDTO dto : request.getItems()) {
            SKU sku = skuRepository.findBySkuCode(dto.getSkuCode())
                    .orElseThrow(() -> new AppException(ErrorCode.SKU_NOT_FOUND));

            int requiredVolume = (int) (dto.getQuantity() * sku.getUnitVolume());

            // Tìm bin còn sức chứa
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

            // Tìm box trong bin phù hợp
            List<Box> availableBoxes = boxRepository.findAvailableBoxes(sku.getId(), sku.getUnitVolume());
            Bin finalTargetBin = targetBin;
            availableBoxes = availableBoxes.stream()
                    .filter(box -> box.getBin().getId().equals(finalTargetBin.getId()))
                    .collect(Collectors.toList());

            int quantityRemaining = dto.getQuantity();

            while (quantityRemaining > 0) {
                Box targetBox = null;
                for (Box box : availableBoxes) {
                    int boxFree = box.getCapacity() - box.getUsedCapacity();
                    if (boxFree >= sku.getUnitVolume()) {
                        targetBox = box;
                        break;
                    }
                }

                // Nếu không có box phù hợp → tạo mới
                if (targetBox == null) {
                    int boxCount = boxRepository.countBoxesInBin(targetBin.getId());
                    String newBoxCode = targetBin.getBinCode() + "-BOX-" + (boxCount + 1);

                    targetBox = Box.builder()
                            .boxCode(newBoxCode)
                            .bin(targetBin)
                            .sku(sku)
                            .capacity(1000) // mặc định
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

                // Cập nhật usedCapacity
                targetBox.setUsedCapacity((int) (targetBox.getUsedCapacity() + canAddQuantity * sku.getUnitVolume()));
                boxRepository.save(targetBox);

                quantityRemaining -= canAddQuantity;
            }

            // Lưu chi tiết đơn hàng
            ImportOrderDetail detail = ItemImportMapper.toDetail(importOrder, sku, dto.getQuantity());
            importOrderDetailRepository.save(detail);
        }

        return ImportItemsResponse.builder()
                .importOrderId(importOrder.getId())
                .importedItems(importedItems)
                .build();
    }
}
