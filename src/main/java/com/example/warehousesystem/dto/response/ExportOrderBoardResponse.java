package com.example.warehousesystem.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExportOrderBoardResponse {
    private Long id;
    private String exportCode;
    private String skuCode;
    private String skuName;

    @JsonFormat(pattern = "yyyy/MM/dd")
    private LocalDateTime createdAt;

    private String source;
    private Integer quantity;
    private String note;
}
