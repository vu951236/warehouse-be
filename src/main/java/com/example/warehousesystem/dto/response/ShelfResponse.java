package com.example.warehousesystem.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShelfResponse {
    private Integer id;
    private String shelfCode;
    private Integer binCount;
    private Integer warehouseId;
    private String warehouseName;
}
