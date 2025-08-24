package com.example.warehousesystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DamagedItemResponse {
    private String barcode;
    private String note;
}
