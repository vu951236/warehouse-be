package com.example.warehousesystem.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchItemRequest {
    private Integer itemId;    // tìm theo ID item
    private Integer boxId;
    private Integer skuId;
    private String barcode;
    private Integer exportOrderId;
    private Integer importOrderId;
}
