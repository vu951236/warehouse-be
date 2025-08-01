package com.example.warehousesystem.controller;

import com.example.warehousesystem.dto.request.StorageStatusRequest;
import com.example.warehousesystem.dto.response.StorageStatusResponse;
import com.example.warehousesystem.service.StorageStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/charts")
@RequiredArgsConstructor
public class StorageStatusController {

    private final StorageStatusService storageStatusService;

    @PostMapping("/storage-status")
    public StorageStatusResponse getStorageStatus(@RequestBody StorageStatusRequest request) {
        return storageStatusService.getStorageStatus(request);
    }
}
