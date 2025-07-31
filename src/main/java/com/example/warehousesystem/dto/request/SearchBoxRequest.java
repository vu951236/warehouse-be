package com.example.warehousesystem.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchBoxRequest {
    private Integer binId;   // tìm theo bin chứa box
    private Integer boxId;   // tìm theo ID box
    private Integer skuId;   // tìm theo sku của box
    private Integer itemId;  // tìm box chứa item cụ thể
}
