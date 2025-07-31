package com.example.warehousesystem.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScanBarcodeDTO {
    private String barcode;     // Mã barcode của item quét được
    private Integer skuId;      // SKU để tìm box phù hợp
    private Integer quantity;   // Số lượng nhập (có thể = 1 nếu từng cái)
}


