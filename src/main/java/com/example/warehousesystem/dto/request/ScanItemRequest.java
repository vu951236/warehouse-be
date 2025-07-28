package com.example.warehousesystem.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
//Quét mã xếp item
public class ScanItemRequest {
    private String barcode;
}
