package com.example.warehousesystem.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BinResponse {
    private Integer id;
    private String binCode;
    private Integer capacity;
    private Integer shelfId;
    private String shelfCode;
}
