package com.example.warehousesystem.dto.request;

import com.example.warehousesystem.entity.Item.Status;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateItemRequest {
    private Integer id;
    private Integer boxId;
    private Integer skuId;
    private String barcode;
    private Status status;
}
