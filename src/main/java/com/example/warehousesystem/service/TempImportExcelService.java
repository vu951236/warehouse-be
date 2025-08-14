package com.example.warehousesystem.service;

import com.example.warehousesystem.dto.ExcelItemDTO;
import com.example.warehousesystem.dto.request.UpdateTempImportRequest;
import com.example.warehousesystem.dto.response.TempImportExcelResponse;
import com.example.warehousesystem.entity.TempImportExcel;
import com.example.warehousesystem.exception.AppException;
import com.example.warehousesystem.exception.ErrorCode;
import com.example.warehousesystem.repository.SKURepository;
import com.example.warehousesystem.repository.TempImportExcelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.warehousesystem.entity.SKU;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TempImportExcelService {

    private final TempImportExcelRepository tempImportExcelRepository;
    private final SKURepository skuRepository;

    @Transactional
    public void saveTempItems(List<ExcelItemDTO> items, Long userId, String source, String note) {
        List<TempImportExcel> entities = items.stream()
                .map(dto -> TempImportExcel.builder()
                        .userId(userId)
                        .skuCode(dto.getSkuCode())
                        .quantity(dto.getQuantity())
                        .source(source)
                        .note(note)
                        .build())
                .toList();

        tempImportExcelRepository.saveAll(entities);
    }


    public List<TempImportExcelResponse> getTempItemsByUser(Long userId) {

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
    public void deleteTempItems(List<Long> ids) {
        tempImportExcelRepository.deleteAllById(ids);
    }
}
