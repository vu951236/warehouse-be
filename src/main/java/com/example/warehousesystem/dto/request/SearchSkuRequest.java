package com.example.warehousesystem.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchSkuRequest {
    private String skuCode;
    private String itemCode;
    private String boxCode;
    private String importCode;
    private String exportCode;
}
