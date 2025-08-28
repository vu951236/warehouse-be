package com.example.warehousesystem.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class StorageDashboardRequest {

    @NotNull
    private Integer warehouseId;
}
