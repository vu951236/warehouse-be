package com.example.warehousesystem.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AllShelfResponse {
    private Integer id;
    private String shelfCode;
    private Long itemCount;
}
