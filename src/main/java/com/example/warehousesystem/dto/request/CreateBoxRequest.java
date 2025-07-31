package com.example.warehousesystem.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateBoxRequest {
    private Integer binId;
    private Integer skuId;
    private Integer capacity;
}
