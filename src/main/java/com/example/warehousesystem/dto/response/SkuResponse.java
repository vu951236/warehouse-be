package com.example.warehousesystem.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkuResponse {
    private Integer id;
    private String skuCode;
    private String name;
    private String type;
    private String color;
    private String size;
    private Float unitVolume;
}
