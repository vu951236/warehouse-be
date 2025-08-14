package com.example.warehousesystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SKUStatusResponse {

    private String skuCode;    // Mã SKU
    private int availableQty;  // Số lượng trạng thái available
    private int queuedQty;     // Số lượng trạng thái queued
}
