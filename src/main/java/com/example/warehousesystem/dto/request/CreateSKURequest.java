package com.example.warehousesystem.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSKURequest {
    private String skuCode;
    private String name;
    private String size;
    private String color;
    private String type;
    private Float unitVolume;
}
