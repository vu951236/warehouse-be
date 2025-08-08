package com.example.warehousesystem.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateBoxRequest {
    private String binCode;
    private String skuCode;
    private Integer capacity;
}
