package com.example.warehousesystem.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExportItemResponse {
    private Integer itemId;
    private String barcode;
    private String skuCode;
    private Integer boxId;
    private String boxCode;
    private String binCode;
    private String shelfCode;
}
