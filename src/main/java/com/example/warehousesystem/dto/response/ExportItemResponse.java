package com.example.warehousesystem.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Xuất hàng
public class ExportItemResponse {
    private Integer itemId;
    private String barcode;
    private String skuCode;
    private Integer boxId;
    private String boxCode;
    private String binCode;
    private String shelfCode;

    // Thêm thông tin xuất
    private String exportCode;
    private LocalDateTime exportDate;
    private String exportDateString;
    private Integer quantity;
}

