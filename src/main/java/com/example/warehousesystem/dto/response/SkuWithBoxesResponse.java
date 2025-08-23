package com.example.warehousesystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkuWithBoxesResponse {
    private Integer id;
    private String skuCode;
    private String name;
    private String size;
    private String color;
    private String type;
    private Float unitVolume;
    private Long totalItemCount;
    private List<BoxItemResponse> boxes;  
}
