package com.example.warehousesystem.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class QualityTrendResponse {
    private LocalDate date;
    private Long damaged;
    private Long returned;
}
