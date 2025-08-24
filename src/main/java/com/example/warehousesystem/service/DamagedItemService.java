package com.example.warehousesystem.service;

import com.example.warehousesystem.dto.request.TransferItemRequest;
import com.example.warehousesystem.dto.response.DamagedItemResponse;
import com.example.warehousesystem.entity.*;
import com.example.warehousesystem.repository.BinRepository;
import com.example.warehousesystem.repository.BoxRepository;
import com.example.warehousesystem.repository.DamagedItemRepository;
import com.example.warehousesystem.repository.ItemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DamagedItemService {

    private final ItemRepository itemRepository;
    private final BoxRepository boxRepository;
    private final BinRepository binRepository;
    private final DamagedItemRepository damagedItemRepository;

    public void markItemDamaged(String barcode) {
        Item item = itemRepository.findByBarcode(barcode)
                .orElseThrow(() -> new RuntimeException("Item không tồn tại: " + barcode));

        // Kiểm tra status phải là AVAILABLE
        if (item.getStatus() != Item.Status.available) {
            throw new RuntimeException("Chỉ có thể chuyển sản phẩm có trạng thái AVAILABLE sang lỗi");
        }

        // Kiểm tra xem đã tồn tại trong damaged_item chưa
        Optional<DamagedItem> existing = damagedItemRepository.findByBarcode(barcode);
        if (existing.isPresent()) {
            throw new RuntimeException("Sản phẩm đã được đánh dấu lỗi trước đó");
        }

        // Tạo bản ghi damaged item
        DamagedItem damagedItem = DamagedItem.builder()
                .sku(item.getSku())
                .barcode(item.getBarcode())
                .markedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        damagedItemRepository.save(damagedItem);

        // Cập nhật lại status item sang DAMAGED
        item.setStatus(Item.Status.damaged);
        itemRepository.save(item);
    }

    @Transactional
    public void transferItemsDamaged(List<TransferItemRequest> requests) {
        for (TransferItemRequest req : requests) {
            // Chỉ xử lý nếu barcode tồn tại trong bảng damaged_item
            DamagedItem damagedItem = damagedItemRepository.findByBarcode(req.getBarcode())
                    .orElseThrow(() -> new RuntimeException(
                            "Sản phẩm chưa được đánh dấu lỗi: " + req.getBarcode()
                    ));

            Item item = itemRepository.findByBarcode(req.getBarcode())
                    .orElseThrow(() -> new RuntimeException("Item không tồn tại: " + req.getBarcode()));

            // Cập nhật status + note riêng cho từng item
            item.setStatus(Item.Status.damaged);
            item.setNote(req.getNote());
            item.setTransferredAt(LocalDateTime.now());

            SKU sku = item.getSku();

            // Tìm box còn trống trong shelf SH31 hoặc SH32
            List<Box> candidateBoxes = boxRepository.findAvailableBoxesForSkuInShelves(
                    sku.getId(), List.of("SH31", "SH32")
            );

            Box targetBox = null;
            for (Box box : candidateBoxes) {
                int free = box.getCapacity() - box.getUsedCapacity();
                if (free >= sku.getUnitVolume()) {
                    targetBox = box;
                    break;
                }
            }

            if (targetBox == null) {
                // Không có box nào đủ chỗ → chọn bin còn dung lượng
                List<Bin> availableBins = binRepository.findBinsWithAvailableCapacityInShelves(
                        List.of("SH31", "SH32")
                );

                Bin targetBin = null;
                for (Bin bin : availableBins) {
                    int usedCapacity = binRepository.getUsedCapacityInBin(bin.getId());
                    if (bin.getCapacity() - usedCapacity >= sku.getUnitVolume()) {
                        targetBin = bin;
                        break;
                    }
                }

                if (targetBin == null) {
                    throw new RuntimeException("Không còn bin đủ dung lượng trong SH31/SH32 cho SKU " + sku.getSkuCode());
                }

                // Tạo mới box trong bin được chọn
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

                targetBox = boxRepository.save(targetBox);
            }

            // Gán item vào box
            item.setBox(targetBox);
            targetBox.setUsedCapacity(targetBox.getUsedCapacity() + sku.getUnitVolume().intValue());

            boxRepository.save(targetBox);
            itemRepository.save(item);

            // Xóa bản ghi damaged_item sau khi xử lý xong
            damagedItemRepository.delete(damagedItem);
        }
    }

    public List<DamagedItemResponse> getAllDamagedItems() {
        return damagedItemRepository.findAll().stream()
                .map(d -> new DamagedItemResponse(d.getBarcode(), d.getNote()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateNoteForDamagedItem(String barcode, String note) {
        DamagedItem damagedItem = damagedItemRepository.findByBarcode(barcode)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm lỗi với barcode: " + barcode));

        // Cập nhật note
        damagedItem.setNote(note);
        damagedItemRepository.save(damagedItem);
    }

    @Transactional
    public void deleteDamagedItem(String barcode) {
        DamagedItem damagedItem = damagedItemRepository.findByBarcode(barcode)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm lỗi với barcode: " + barcode));

        damagedItemRepository.delete(damagedItem);
    }




}
