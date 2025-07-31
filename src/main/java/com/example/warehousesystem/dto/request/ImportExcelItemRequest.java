package com.example.warehousesystem.dto.request;

import com.example.warehousesystem.dto.ExcelItemDTO;
import com.example.warehousesystem.entity.ImportOrder;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Nhập hàng bằng Excel (gồm nhiều item)
public class ImportExcelItemRequest {
    private List<ExcelItemDTO> items; // Danh sách các item từ Excel
    private ImportOrder.Source source;
    private Integer userId;           // Người nhập
    private String note;              // Ghi chú
}

