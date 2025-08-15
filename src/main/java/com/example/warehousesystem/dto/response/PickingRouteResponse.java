package com.example.warehousesystem.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PickingRouteResponse {
    private String skuCode;
    private String skuName;
    private String boxCode;
    private String binCode;
    private String shelfCode;
    private String warehouseName;
    private Integer quantityPicked;
    private List<String> barcodes;
}
