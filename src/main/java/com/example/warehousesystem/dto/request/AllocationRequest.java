package com.example.warehousesystem.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AllocationRequest {
    private String skuCode;
    private Integer quantity;
}



