package com.example.warehousesystem.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateBinRequest {
    private String binCode;         // ID của Bin cần cập nhật
    private Integer capacity;   // Sức chứa mới
}
