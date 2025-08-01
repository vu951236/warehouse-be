package com.example.warehousesystem.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScanBarcodeDTO {
    private String barcode;
    private Integer skuId;
    private Integer quantity;
}


