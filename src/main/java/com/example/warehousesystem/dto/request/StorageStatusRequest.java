package com.example.warehousesystem.dto.request;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StorageStatusRequest {
    private Integer warehouseId; // lọc theo warehouse
}
