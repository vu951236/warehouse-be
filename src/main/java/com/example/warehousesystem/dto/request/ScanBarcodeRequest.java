package com.example.warehousesystem.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScanBarcodeRequest {
    private String barcode;
}
