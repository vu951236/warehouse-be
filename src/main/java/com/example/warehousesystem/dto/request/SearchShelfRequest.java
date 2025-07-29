package com.example.warehousesystem.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
//Tìm kiếm kệ hàng
public class SearchShelfRequest {
    private Integer shelfId;       // tìm theo ID kệ
    private Integer boxId;         // tìm theo box
    private Integer binId;         // tìm theo bin
    private Integer skuId;         // tìm theo SKU
    private Integer warehouseId;   // tìm theo warehouse
}
