package com.example.warehousesystem.service;

import com.example.warehousesystem.dto.request.ExportItemRequest;
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
public class ExportMultipleItemsService {

    private final ExportOrderRepository exportOrderRepository;
    private final ExportOrderDetailRepository exportOrderDetailRepository;
    private final ItemRepository itemRepository;
    private final BoxRepository boxRepository;
    private final UserRepository userRepository;

    @Transactional
    public List<ExportItemResponse> exportMultipleItems(ExportItemRequest request) {
        List<ExportItemResponse> responses = new ArrayList<>();

        // 1. Lấy hoặc tạo ExportOrder
        ExportOrder exportOrder = exportOrderRepository.findByExportCode(request.getExportCode())
                .orElseGet(() -> {
                    User createdBy = userRepository.findById(request.getUserId())
                            .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng ID: " + request.getUserId()));

                    ExportOrder order = new ExportOrder();
                    order.setExportCode(request.getExportCode());
                    order.setCreatedBy(createdBy); // bây giờ set đúng kiểu User
                    order.setCreatedAt(LocalDateTime.now());
                    order.setStatus(ExportOrder.Status.draft);
                    order.setSource(ExportOrder.Source.manual);
                    order.setUrgent(false);
                    return exportOrderRepository.save(order);
                });


        // 2. Lấy danh sách item từ barcodes
        List<Item> items = itemRepository.findItemsByBarcodes(request.getBarcodes());

        for (String barcode : request.getBarcodes()) {
            Item item = items.stream()
                    .filter(i -> i.getBarcode().equals(barcode))
                    .findFirst()
                    .orElse(null);

            // Nếu item không tồn tại hoặc không ở trạng thái IN_STOCK thì bỏ qua
            if (item == null || item.getStatus() != Item.Status.available) {
                continue;
            }

            // 3. Tạo ExportOrderDetail
            ExportOrderDetail detail = new ExportOrderDetail();
            detail.setExportOrder(exportOrder);
            detail.setSku(item.getSku());
            detail.setQuantity(1); // mỗi barcode = 1 sản phẩm
            exportOrderDetailRepository.save(detail);

            // 4. Cập nhật trạng thái item
            item.setStatus(Item.Status.exported);
            itemRepository.save(item);

            // 5. Giảm usedCapacity của Box
            Box box = item.getBox();
            if (box != null && box.getUsedCapacity() > 0) {
                box.setUsedCapacity(box.getUsedCapacity() - 1);
                boxRepository.save(box);
            }

            // 6. Thêm vào danh sách response
            responses.add(ItemExportMapper.toResponse(item));
        }

        return responses;
    }
}
