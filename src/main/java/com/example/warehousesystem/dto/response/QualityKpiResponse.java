package com.example.warehousesystem.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class QualityKpiResponse {
    private Long totalDamaged;
    private Double damagedPercentage;
    private Long totalReturned;
}
