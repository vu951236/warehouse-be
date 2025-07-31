package com.example.warehousesystem.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateBoxRequest {
    private Integer id;         // ID của Box cần cập nhật
    private Integer binId;
    private Integer skuId;
    private Integer capacity;
}
