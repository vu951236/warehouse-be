package com.example.warehousesystem.dto.request;

import com.example.warehousesystem.entity.Item;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchItemRequest {
    private Integer itemId;    // tìm theo ID item
    private Integer boxId;
    private Integer skuId;
    private String barcode;
    private Item.Status status;
    private Integer exportOrderId;
    private Integer importOrderId;
}
