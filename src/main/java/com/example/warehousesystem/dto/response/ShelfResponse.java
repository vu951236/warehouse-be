package com.example.warehousesystem.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShelfResponse {
    private Integer id;
    private String shelfCode;
    private Integer binCount;
    private List<String> binCodes;
}
