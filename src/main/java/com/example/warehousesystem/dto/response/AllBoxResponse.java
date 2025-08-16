package com.example.warehousesystem.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AllBoxResponse {
    private Integer id;
    private String boxCode;
    private Long itemCount;
}
