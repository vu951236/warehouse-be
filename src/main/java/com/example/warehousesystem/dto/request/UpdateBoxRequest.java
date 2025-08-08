package com.example.warehousesystem.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateBoxRequest {
    private String boxCode;     // mã box cũ
    private String binCode;     // mã bin mới
    private Integer capacity;
}
