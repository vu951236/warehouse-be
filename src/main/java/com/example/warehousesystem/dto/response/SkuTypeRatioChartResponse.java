package com.example.warehousesystem.dto.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SkuTypeRatioChartResponse {
    private String skuName;
    private Long totalQuantity;
}
