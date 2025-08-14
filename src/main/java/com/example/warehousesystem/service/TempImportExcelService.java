package com.example.warehousesystem.service;

import com.example.warehousesystem.dto.ExcelItemDTO;
import com.example.warehousesystem.dto.response.TempImportExcelResponse;
import com.example.warehousesystem.entity.TempImportExcel;
import com.example.warehousesystem.entity.SKU;
import com.example.warehousesystem.repository.SKURepository;
import com.example.warehousesystem.repository.TempImportExcelRepository;
import com.example.warehousesystem.repository.UserRepository;
import com.example.warehousesystem.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TempImportExcelService {

    private final TempImportExcelRepository tempImportExcelRepository;
    private final SKURepository skuRepository;
    private final UserRepository userRepository;

    @Transactional
    public void saveTempItems(List<ExcelItemDTO> items) {
        Long userId = Long.valueOf(getCurrentUserId());

        List<TempImportExcel> entities = items.stream()
                .map(dto -> TempImportExcel.builder()
                        .userId(userId)
                        .skuCode(dto.getSkuCode())
                        .quantity(dto.getQuantity())
                        .source(dto.getSource())
                        .note(dto.getNote())
                        .build())
                .toList();

        tempImportExcelRepository.saveAll(entities);
    }

    public List<TempImportExcelResponse> getTempItemsByUser() {
        Long userId = Long.valueOf(getCurrentUserId());

        return tempImportExcelRepository.findByUserId(userId)
                .stream()
                .map(entity -> {
                    String skuName = skuRepository.findBySkuCode(entity.getSkuCode())
                            .map(SKU::getName)
                            .orElse(null);

                    return TempImportExcelResponse.builder()
                            .id(entity.getId())
                            .userId(entity.getUserId())
                            .skuCode(entity.getSkuCode())
                            .skuName(skuName)
                            .quantity(entity.getQuantity())
                            .source(entity.getSource())
                            .note(entity.getNote())
                            .createdAt(entity.getCreatedAt())
                            .build();
                })
                .toList();
    }

    @Transactional
    public void deleteTempItems(List<Long> ids) {
        tempImportExcelRepository.deleteAllById(ids);
    }

    private Integer getCurrentUserId() {
        var context = SecurityContextHolder.getContext();
        var username = context.getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        return user.getId();
    }
}
