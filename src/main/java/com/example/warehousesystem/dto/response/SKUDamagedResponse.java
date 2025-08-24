package com.example.warehousesystem.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SKUDamagedResponse {
    private Long id;
    private String skuCode;
    private String name;
    private String size;
    private String color;
    private String type;
    private Float unitVolume;
    private Long damagedCount;
}
