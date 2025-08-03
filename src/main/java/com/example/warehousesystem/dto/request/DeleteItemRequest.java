package com.example.warehousesystem.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeleteItemRequest {
    private String barcode;
}
