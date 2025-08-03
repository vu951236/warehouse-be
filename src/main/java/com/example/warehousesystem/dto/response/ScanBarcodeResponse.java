package com.example.warehousesystem.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScanBarcodeResponse {
    private String skuCode;
    private Integer itemId;
    private String type;
    private String color;
    private String size;
    private String boxCode;
    private String binCode;
    private String shelfCode;
    private String warehouse;
    private String status;
}
