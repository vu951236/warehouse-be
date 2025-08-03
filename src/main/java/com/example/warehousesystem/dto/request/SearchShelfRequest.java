package com.example.warehousesystem.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
//Tìm kiếm kệ hàng
public class SearchShelfRequest {
    private String shelfCode;       // tìm theo ID kệ
    private String boxCode;         // tìm theo box
    private String binCode;         // tìm theo bin
    private String skuCode;         // tìm theo SKU
    private Integer warehouseId;   // tìm theo warehouse
}
