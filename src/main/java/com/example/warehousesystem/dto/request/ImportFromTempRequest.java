package com.example.warehousesystem.dto.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportFromTempRequest {
    private List<Long> tempIds;
}
