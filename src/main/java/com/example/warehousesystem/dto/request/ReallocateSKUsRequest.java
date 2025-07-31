package com.example.warehousesystem.dto.request;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReallocateSKUsRequest {
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private Integer numberOfSKUs; // số SKU muốn phân bổ
}
