package com.example.warehousesystem.dto.response;

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
    private LocalDateTime createdAt;
    private Integer quantity;
}
