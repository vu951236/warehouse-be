package com.example.warehousesystem.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateBoxRequest {
    private String boxCode;         // ID của Box cần cập nhật
    private Integer capacity;
}
