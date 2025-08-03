package com.example.warehousesystem.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchBinRequest {
    private String shelfCode;  // tìm theo kệ
    private String binCode;    // tìm theo bin
    private String boxCode;    // tìm theo box
    private String skuCode;    // tìm theo sku
}
