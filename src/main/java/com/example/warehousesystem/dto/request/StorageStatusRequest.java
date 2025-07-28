package com.example.warehousesystem.dto.request;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
//Tình trạng sức chứa
public class StorageStatusRequest {
    private Integer warehouseId; // lọc theo warehouse
}
