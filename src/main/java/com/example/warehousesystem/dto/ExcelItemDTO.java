package com.example.warehousesystem.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExcelItemDTO {
    private String barcode;     // Mã barcode của item
    private Integer skuId;      // SKU để tra unitVolume và box phù hợp
    private Integer quantity;   // Số lượng nhập
}


