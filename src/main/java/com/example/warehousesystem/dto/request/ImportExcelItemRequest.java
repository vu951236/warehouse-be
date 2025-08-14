package com.example.warehousesystem.dto.request;

import com.example.warehousesystem.dto.ExcelItemDTO;
import com.example.warehousesystem.entity.ImportOrder;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportExcelItemRequest {
    private List<ExcelItemDTO> items;
}

