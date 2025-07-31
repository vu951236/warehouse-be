package com.example.warehousesystem.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchSkuRequest {
    private Integer skuId;
    private Integer itemId;
    private Integer boxId;
    private Integer importOrderId;
    private Integer exportOrderId;
}
