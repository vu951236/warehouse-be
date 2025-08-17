package com.example.warehousesystem.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TempImportExcelResponse {
    private Long id;
    private Long userId;
    private String skuCode;
    private String skuName;
    private Integer quantity;
    private String source;
    private String note;
    private String importCode;

    private String size;
    private String color;
    private String type;
    private Float unitVolume;

    @JsonFormat(pattern = "yyyy/MM/dd")
    private LocalDateTime createdAt;
}
