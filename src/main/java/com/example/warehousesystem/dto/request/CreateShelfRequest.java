package com.example.warehousesystem.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateShelfRequest {
    private String shelfCode;
    private Integer warehouseId;
}
