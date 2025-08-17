package com.example.warehousesystem.service;

import com.example.warehousesystem.dto.ExcelItemDTO;
import com.example.warehousesystem.dto.request.UpdateTempImportRequest;
import com.example.warehousesystem.dto.response.TempImportExcelResponse;
import com.example.warehousesystem.exception.AppException;
import com.example.warehousesystem.exception.ErrorCode;
import com.example.warehousesystem.entity.TempImportExcel;
import com.example.warehousesystem.mapper.ItemImportMapper;
import com.example.warehousesystem.repository.SKURepository;
import com.example.warehousesystem.repository.TempImportExcelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.warehousesystem.entity.SKU;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import com.example.warehousesystem.entity.User;
import com.example.warehousesystem.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class TempImportExcelService {

    private final TempImportExcelRepository tempImportExcelRepository;
    private final SKURepository skuRepository;
    private final UserRepository userRepository;

    private Long getCurrentUserId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        return user.getId().longValue();
    }

    @Transactional
    public void saveTempItems(List<ExcelItemDTO> items) {
        Long userId = getCurrentUserId();

        String importCode = ItemImportMapper.generateImportCode();

        List<TempImportExcel> entities = items.stream()
                .map(dto -> TempImportExcel.builder()
                        .userId(userId)
                        .skuCode(dto.getSkuCode())
                        .quantity(dto.getQuantity())
                        .source(dto.getSource())
                        .note(dto.getNote())
                        .importCode(importCode)
                        .build())
                .toList();

        tempImportExcelRepository.saveAll(entities);
    }

    public List<TempImportExcelResponse> getTempItemsByUser() {
        Long userId = getCurrentUserId();

        return tempImportExcelRepository.findByUserId(userId)
                .stream()
                .map(entity -> {
                    SKU sku = skuRepository.findBySkuCode(entity.getSkuCode()).orElse(null);

                    return TempImportExcelResponse.builder()
                            .id(entity.getId())
                            .userId(entity.getUserId())
                            .skuCode(entity.getSkuCode())
                            .skuName(sku != null ? sku.getName() : null)
                            .quantity(entity.getQuantity())
                            .source(entity.getSource())
                            .note(entity.getNote())
                            .importCode(entity.getImportCode())
                            .size(sku != null ? sku.getSize() : null)
                            .color(sku != null ? sku.getColor() : null)
                            .type(sku != null ? sku.getType() : null)
                            .unitVolume(sku != null ? sku.getUnitVolume() : null)
                            .createdAt(entity.getCreatedAt())
                            .build();
                })
                .toList();
    }


    @Transactional
    public void updateTempImport(UpdateTempImportRequest request) {
        TempImportExcel temp = tempImportExcelRepository.findById(request.getId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        // Cho phép đổi SKU nếu có truyền và phải tồn tại trong bảng SKU
        if (request.getSkuCode() != null && !request.getSkuCode().isBlank()) {
            boolean exists = skuRepository.findBySkuCode(request.getSkuCode()).isPresent();
            if (!exists) {
                throw new AppException(ErrorCode.SKU_NOT_FOUND);
            }
            temp.setSkuCode(request.getSkuCode().trim());
        }

        if (request.getQuantity() != null) {
            if (request.getQuantity() <= 0) throw new AppException(ErrorCode.INVALID_REQUEST);
            temp.setQuantity(request.getQuantity());
        }

        if (request.getNote() != null) temp.setNote(request.getNote());
        if (request.getSource() != null) temp.setSource(request.getSource());

        tempImportExcelRepository.save(temp);
    }

    @Transactional
    public void deleteTempItem(Long id) {
        if (!tempImportExcelRepository.existsById(id)) {
            throw new AppException(ErrorCode.NOT_FOUND);
        }
        tempImportExcelRepository.deleteById(id);
    }


}
