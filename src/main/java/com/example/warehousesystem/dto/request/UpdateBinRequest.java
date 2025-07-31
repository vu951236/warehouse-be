package com.example.warehousesystem.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateBinRequest {
    private Integer id;         // ID của Bin cần cập nhật
    private String binCode;     // Mã Bin mới
    private Integer capacity;   // Sức chứa mới
    private Integer shelfId;    // ID kệ chứa bin này
}
