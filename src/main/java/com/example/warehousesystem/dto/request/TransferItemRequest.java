package com.example.warehousesystem.dto.request;

import lombok.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferItemRequest {
    private String barcode;
    private String note;
}
