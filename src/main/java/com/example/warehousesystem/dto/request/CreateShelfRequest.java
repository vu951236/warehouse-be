package com.example.warehousesystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateShelfRequest {

    @NotBlank(message = "Mã kệ không được để trống")
    private String shelfCode;

    @NotNull(message = "ID kho không được để trống")
    private Integer warehouseId;
}
