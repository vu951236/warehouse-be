package com.example.warehousesystem.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImportSingleItemRequest {
    private Integer userId;     // ID người nhập
    private String skuCode;  // Mã SKU
    private String source;   // Nguồn nhập (factory, returnGoods)
    private String note;     // Ghi chú
}
