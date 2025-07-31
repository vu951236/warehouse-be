package com.example.warehousesystem.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateSkuRequest {
    private Integer id; // ID của SKU cần sửa
    private String skuCode;
    private String name;
    private String size;
    private String color;
    private String type;
    private Float unitVolume;
}
