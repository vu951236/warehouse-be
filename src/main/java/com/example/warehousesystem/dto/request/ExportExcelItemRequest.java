package com.example.warehousesystem.dto.request;

import com.example.warehousesystem.dto.ExcelItemDTO;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
//Xuất hàng bằng excel
public class ExportExcelItemRequest {
    private String exportCode;
    private List<ExcelItemDTO> items;//file ExcelItemDTO
    private Integer userId;
}
