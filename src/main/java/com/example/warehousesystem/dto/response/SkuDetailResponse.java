package com.example.warehousesystem.dto.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SkuDetailResponse {
    private Integer skuId;
    private String skuCode;
    private Long itemCount;
    private String name;
    private String size;
    private String color;
    private String type;
    private Double unitVolume;
}
