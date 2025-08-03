package com.example.warehousesystem.dto.request;

import com.example.warehousesystem.entity.Item;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchItemRequest {
    private String boxCode;
    private String skuCode;
    private String barcode;
    private Item.Status status;
    private String exportCode;
    private String importCode;
}
