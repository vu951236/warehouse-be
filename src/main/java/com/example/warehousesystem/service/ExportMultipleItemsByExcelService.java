package com.example.warehousesystem.service;

import com.example.warehousesystem.dto.ExcelItemDTO;
import com.example.warehousesystem.dto.request.ExportExcelItemRequest;
import com.example.warehousesystem.dto.response.ExportItemResponse;
import com.example.warehousesystem.entity.*;
import com.example.warehousesystem.mapper.ItemExportMapper;
import com.example.warehousesystem.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExportMultipleItemsByExcelService {

    private final ExportOrderRepository exportOrderRepository;
    private final ExportOrderDetailRepository exportOrderDetailRepository;
    private final ItemRepository itemRepository;
    private final BoxRepository boxRepository;
    private final UserRepository userRepository;
    private final SKURepository skuRepository;

    @Transactional
    public List<ExportItemResponse> exportMultipleItemsByExcel(ExportExcelItemRequest request) {
        List<ExportItemResponse> responses = new ArrayList<>();

        // 1. Lấy hoặc tạo ExportOrder
        ExportOrder exportOrder = exportOrderRepository.findByExportCode(request.getExportCode())
                .orElseGet(() -> {
                    User createdBy = userRepository.findById(request.getUserId())
                            .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng ID: " + request.getUserId()));

                    ExportOrder order = new ExportOrder();
                    order.setExportCode(request.getExportCode());
                    order.setCreatedBy(createdBy);
                    order.setCreatedAt(LocalDateTime.now());
                    order.setStatus(ExportOrder.Status.draft);
                    order.setSource(ExportOrder.Source.manual);
                    order.setUrgent(false);
                    return exportOrderRepository.save(order);
                });

        // 2. Lặp qua từng dòng trong file Excel (SKU + quantity)
        for (ExcelItemDTO excelItem : request.getItems()) {
            // Lấy SKU
            SKU sku = skuRepository.findBySkuCode(excelItem.getSkuCode())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy SKU: " + excelItem.getSkuCode()));

            // 3. Lấy danh sách item còn tồn kho theo SKU, đủ số lượng
            List<Item> items = itemRepository.findAvailableItemsBySku(sku.getId(), excelItem.getQuantity());

            if (items.size() < excelItem.getQuantity()) {
                throw new RuntimeException("Không đủ hàng tồn kho cho SKU: " + excelItem.getSkuCode());
            }

            // 4. Tạo ExportOrderDetail
            ExportOrderDetail detail = new ExportOrderDetail();
            detail.setExportOrder(exportOrder);
            detail.setSku(sku);
            detail.setQuantity(excelItem.getQuantity());
            exportOrderDetailRepository.save(detail);

            // 5. Xuất từng item & cập nhật box
            for (Item item : items) {
                item.setStatus(Item.Status.exported);
                itemRepository.save(item);

                Box box = item.getBox();
                if (box != null && box.getUsedCapacity() > 0) {
                    box.setUsedCapacity(box.getUsedCapacity() - 1);
                    boxRepository.save(box);
                }

                responses.add(ItemExportMapper.toResponse(item));
            }
        }

        return responses;
    }
}
