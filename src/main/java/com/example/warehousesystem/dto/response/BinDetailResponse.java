package com.example.warehousesystem.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BinDetailResponse {
    private Integer id;
    private String binCode;
    private Long itemCount;
    private Double utilizationRate;
}
