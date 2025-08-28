package com.example.warehousesystem.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TopDamagedSkuResponse {
    private String skuCode;
    private Long damagedQty;
    private Double damagedPercentage;
}
