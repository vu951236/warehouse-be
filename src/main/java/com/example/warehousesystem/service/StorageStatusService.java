package com.example.warehousesystem.service;

import com.example.warehousesystem.dto.request.StorageStatusRequest;
import com.example.warehousesystem.dto.response.StorageStatusResponse;
import com.example.warehousesystem.mapper.StorageStatusMapper;
import com.example.warehousesystem.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StorageStatusService {

    private final WarehouseRepository warehouseRepository;

    public StorageStatusResponse getStorageStatus(StorageStatusRequest request) {
        Object[] result = warehouseRepository.getWarehouseStorageStatusById(request.getWarehouseId());

        if (result == null || result.length < 5) {
            throw new RuntimeException("Không tìm thấy dữ liệu sức chứa kho.");
        }

        String warehouseName = (String) result[0];
        double usedCapacity = result[1] != null ? ((Number) result[1]).doubleValue() : 0.0;
        int shelfCount = result[2] != null ? ((Number) result[2]).intValue() : 0;
        double totalBinCount = result[3] != null ? ((Number) result[3]).doubleValue() : 0.0;
        double binCapacity = result[4] != null ? ((Number) result[4]).doubleValue() : 0.0;

        return StorageStatusMapper.toResponse(
                warehouseName,
                usedCapacity,
                shelfCount,
                totalBinCount,
                binCapacity
        );
    }
}
