package com.example.warehousesystem.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
//Phân bổ hàng tối ưu
public class AllocationRequest {
    private String skuCode;
    private Integer quantity;
}



