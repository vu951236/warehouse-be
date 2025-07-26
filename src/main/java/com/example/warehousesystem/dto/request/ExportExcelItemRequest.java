package com.example.warehousesystem.dto.request;

import com.example.warehousesystem.dto.ExcelItemDTO;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExportExcelItemRequest {
    private Integer exportOrderId;
    private List<ExcelItemDTO> items;
    private Integer userId;
}
