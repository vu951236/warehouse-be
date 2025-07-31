package com.example.warehousesystem.dto.response;

import com.example.warehousesystem.entity.Item;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ItemResponse {
    private Integer id;
    private String barcode;
    private String skuCode;
    private String skuName;
    private String boxCode;
    private Item.Status status;
    private LocalDateTime createdAt;
}
