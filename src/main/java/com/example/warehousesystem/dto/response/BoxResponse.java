package com.example.warehousesystem.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoxResponse {
    private Integer id;
    private String boxCode;
    private Integer binId;
    private String binCode;
    private Integer skuId;
    private String skuCode;
    private String skuName;
    private Integer capacity;
    private Integer usedCapacity;
}
