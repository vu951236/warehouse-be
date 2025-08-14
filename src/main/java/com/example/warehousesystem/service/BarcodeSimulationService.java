package com.example.warehousesystem.service;

import com.example.warehousesystem.entity.ImportOrder;
import com.example.warehousesystem.entity.Item;
import com.example.warehousesystem.exception.AppException;
import com.example.warehousesystem.exception.ErrorCode;
import com.example.warehousesystem.repository.ImportOrderRepository;
import com.example.warehousesystem.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BarcodeSimulationService {

    private final ImportOrderRepository importOrderRepository;
    private final ItemRepository itemRepository;

    /**
     * Giả lập bắn mã cho tất cả Item thuộc Import Order
     * @param importOrderId ID phiếu nhập
     * @param count Số lượng barcode giả lập muốn lấy
     * @return Danh sách barcode
     */
    public List<String> simulateForImportOrder(Integer importOrderId, int count) {
        // 1. Check ImportOrder tồn tại
        ImportOrder order = importOrderRepository.findById(importOrderId)
                .orElseThrow(() -> new AppException(ErrorCode.IMPORT_ORDER_NOT_FOUND));

        // 2. Lấy tất cả Item thuộc ImportOrder
        List<Item> items = itemRepository.findByImportOrderId(importOrderId);
        if (items.isEmpty()) {
            throw new AppException(ErrorCode.ITEM_NOT_FOUND);
        }

        // 3. Trộn danh sách và chọn số lượng yêu cầu
        Collections.shuffle(items);
        return items.stream()
                .limit(count)
                .map(Item::getBarcode)
                .collect(Collectors.toList());
    }
}
