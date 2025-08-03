package com.example.warehousesystem.logic;

import com.example.warehousesystem.entity.*;
import com.example.warehousesystem.exception.AppException;
import com.example.warehousesystem.exception.ErrorCode;
import com.example.warehousesystem.repository.BinRepository;
import com.example.warehousesystem.repository.BoxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PutAwayOptimizer {

    private final BinRepository binRepository;
    private final BoxRepository boxRepository;

    public Box findOptimalBox(SKU sku, int quantity) {
        int requiredVolume = (int) (sku.getUnitVolume() * quantity);

        // 1. Tìm Bin còn sức chứa phù hợp
        List<Bin> candidateBins = binRepository.findBinsWithAvailableCapacity();
        if (candidateBins.isEmpty()) {
            throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "No Bin with available capacity");
        }

        for (Bin bin : candidateBins) {
            Integer usedCapacity = binRepository.getUsedCapacityInBin(bin.getId());
            int binFree = bin.getCapacity() - usedCapacity;
            if (binFree < requiredVolume) continue;

            // 2. Tìm Box phù hợp trong Bin đó
            List<Box> boxes = boxRepository.findAvailableBoxes(sku.getId(), requiredVolume);
            for (Box box : boxes) {
                if (box.getBin().getId().equals(bin.getId())) {
                    return box; // Trả về box có sẵn
                }
            }

            // 3. Nếu không có box phù hợp → tạo box mới
            Box newBox = Box.builder()
                    .bin(bin)
                    .sku(sku)
                    .capacity(bin.getCapacity()) // giả định mỗi box = 1 bin
                    .usedCapacity(0)
                    .isDeleted(false)
                    .build();
            return newBox; // tạo box mới sau này gọi save()
        }

        throw new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Không thể phân bổ item vào kho");
    }
}
