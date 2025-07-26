package com.example.warehousesystem.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScanItemResponse {
    private String barcode;
    private String skuCode;
    private String skuName;
    private String binCode;
    private Integer boxId;
    private String itemStatus;

    private boolean assigned;
    private Integer exportOrderId;
    private String exportOrderCode;
    private Integer detailId;
    private String message;
}
