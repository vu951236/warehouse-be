package com.example.warehousesystem.service;

import com.example.warehousesystem.dto.request.StorageStatusRequest;
import com.example.warehousesystem.dto.response.StorageStatusResponse;
import com.example.warehousesystem.dto.response.WarehouseStorageStatusProjection;
import com.example.warehousesystem.mapper.StorageStatusMapper;
import com.example.warehousesystem.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StorageStatusService {

    private final WarehouseRepository warehouseRepository;

    public StorageStatusResponse getStorageStatus(StorageStatusRequest request) {
        WarehouseStorageStatusProjection projection = warehouseRepository.getWarehouseStorageStatusById(request.getWarehouseId());

        if (projection == null) {
            throw new RuntimeException("Không tìm thấy dữ liệu sức chứa kho.");
        }

        return StorageStatusMapper.toResponse(
                projection.getWarehouseName(),
                projection.getUsedCapacity(),
                projection.getShelfCount(),
                projection.getTotalBinCount(),
                projection.getBinCapacity()
        );
    }
}
