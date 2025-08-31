package com.example.warehousesystem.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Xuất hàng
public class ExportExcelResponse {
    private String skuCode;

    // Thêm thông tin xuất
    private String exportCode;
    private String exportDate;
    private Integer quantity;
    private String note;

}

