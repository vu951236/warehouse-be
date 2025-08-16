package com.example.warehousesystem.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AllBinResponse {
    private Integer id;
    private String binCode;
    private Long itemCount;
}
