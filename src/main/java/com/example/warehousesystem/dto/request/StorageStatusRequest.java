package com.example.warehousesystem.dto.request;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
//Tình trạng sức chứa
public class StorageStatusRequest {
    private Integer warehouseId;// lọc theo warehouse
    private String startDate; // ví dụ: "2025-01-01"
    private String endDate;   // ví dụ: "2025-12-31"
}
