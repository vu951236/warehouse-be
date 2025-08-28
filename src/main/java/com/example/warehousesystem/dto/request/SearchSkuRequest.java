package com.example.warehousesystem.dto.request;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchSkuRequest {
    private String skuCode;
    private String size;
    private String color;
    private String type;
    private Double minUnitVolume;
    private Double maxUnitVolume;
}
