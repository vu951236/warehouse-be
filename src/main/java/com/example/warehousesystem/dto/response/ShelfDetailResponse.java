package com.example.warehousesystem.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShelfDetailResponse {
    private Integer id;
    private String shelfCode;
    private Long itemCount;
    private Double utilizationRate;
}
