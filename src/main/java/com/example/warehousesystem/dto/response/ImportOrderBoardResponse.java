package com.example.warehousesystem.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImportOrderBoardResponse {
    private String importCode;
    private String skuCode;
    private String skuName;
    private LocalDateTime createdAt;
    private Integer quantity;
}
