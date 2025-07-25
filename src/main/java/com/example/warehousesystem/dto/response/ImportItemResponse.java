package com.example.warehousesystem.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportItemResponse {
    private Integer itemId;
    private String barcode;
    private Integer boxId;
    private String boxCode;
    private String skuCode;
    private String shelfCode;
    private String binCode;
}

