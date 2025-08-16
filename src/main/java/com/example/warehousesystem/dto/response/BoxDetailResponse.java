package com.example.warehousesystem.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoxDetailResponse {
    private String boxCode;
    private Long itemCount;
    private Double utilization;
    private String skuName;
    private Long skuItemCount;
}
