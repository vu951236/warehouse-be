package com.example.warehousesystem.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SKUReallocationResponse {
    private Integer skuId;
    private String skuCode;
    private String skuName;
    private Integer pickCount;
    private String suggestedShelfCode; // tên kệ gợi ý phân bổ lại
}
