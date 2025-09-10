package com.example.warehousesystem.service;


import com.example.warehousesystem.dto.ScanBarcodeDTO;
import com.example.warehousesystem.dto.request.ImportScanBarcodeRequest;
import com.example.warehousesystem.entity.*;
import com.example.warehousesystem.mapper.ItemImportMapper;
import com.example.warehousesystem.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ImportScanBarcodeService {

    private final UserRepository userRepository;
    private final TempImportExcelRepository tempImportExcelRepository;

    private Long getCurrentUserId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        return user.getId().longValue();
    }

    @Transactional
    public void saveScannedBarcodes(ImportScanBarcodeRequest request) {
        Long userId = getCurrentUserId();

        String importCode = ItemImportMapper.generateImportCode();

        LocalDate importDate = request.getImportDate() != null
                ? request.getImportDate()
                : LocalDate.now();

        Map<String, Integer> skuCountMap = new HashMap<>();
        for (ScanBarcodeDTO dto : request.getScannedItems()) {
            String skuCode = extractSkuCodeFromBarcode(dto.getBarcode());
            skuCountMap.put(skuCode, skuCountMap.getOrDefault(skuCode, 0) + 1);
        }

        List<TempImportExcel> entities = skuCountMap.entrySet().stream()
                .map(entry -> TempImportExcel.builder()
                        .userId(userId)
                        .skuCode(entry.getKey())
                        .quantity(entry.getValue())
                        .source(String.valueOf(request.getSource()))
                        .note(request.getNote())
                        .importCode(importCode)
                        .createdAt(importDate)
                        .build())
                .toList();

        tempImportExcelRepository.saveAll(entities);
    }


    private String extractSkuCodeFromBarcode(String barcode) {
        if (barcode == null || !barcode.contains("-")) {
            throw new RuntimeException("Invalid barcode: " + barcode);
        }
        return barcode.split("-")[0];
    }
}
