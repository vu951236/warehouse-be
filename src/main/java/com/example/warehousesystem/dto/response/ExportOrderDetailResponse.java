package com.example.warehousesystem.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExportOrderDetailResponse {
    private Integer skuId;
    private String skuCode;
    private String skuName;
    private Integer quantity;
}
