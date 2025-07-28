package com.example.warehousesystem.dto.request;

import com.example.warehousesystem.dto.ExcelItemDTO;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
//Nhập hàng bằng excel
public class ImportExcelItemRequest {
    private Integer importOrderId;
    private List<ExcelItemDTO> items;//file ExcelItemDTO
    private Integer userId;
}

