package com.example.warehousesystem.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchBinRequest {
    private Integer shelfId;  // tìm theo kệ
    private Integer binId;    // tìm theo bin
    private Integer boxId;    // tìm theo box
    private Integer skuId;    // tìm theo sku
}
