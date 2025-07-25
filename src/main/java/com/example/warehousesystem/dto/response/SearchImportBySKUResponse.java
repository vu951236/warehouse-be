package com.example.warehousesystem.dto.response;

import lombok.*;

        import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchImportBySKUResponse {
    private Integer importOrderId;
    private String importDate;
    private Integer quantity;
    private Integer receivedQuantity;
    private String status;
    private String createdBy;
    private String warehouseName;
}
