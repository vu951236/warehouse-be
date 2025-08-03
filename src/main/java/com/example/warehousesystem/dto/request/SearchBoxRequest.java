package com.example.warehousesystem.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchBoxRequest {
    private String binCode;   // tìm theo bin chứa box
    private String boxCode;   // tìm theo ID box
    private String skuCode;   // tìm theo sku của box
    private String barcode;  // tìm box chứa item cụ thể
}
