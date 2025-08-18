package com.example.warehousesystem.dto.response;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImportOrderBoardResponse {
    private Long id;

    private String importCode;
    private String skuCode;
    private String skuName;

    @JsonFormat(pattern = "yyyy/MM/dd")
    private LocalDateTime createdAt;

    private Integer quantity;
    private String size;
    private String color;
    private String type;
    private Float unitVolume;
    private String source;
    private String note;
}
