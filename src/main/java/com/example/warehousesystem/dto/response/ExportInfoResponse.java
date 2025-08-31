package com.example.warehousesystem.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExportInfoResponse {
    private String exportCode;

    @JsonFormat(pattern = "yyyy/MM/dd")
    private LocalDateTime exportDate;

    private String note;
    private List<PickingRouteResponse> pickingRoutes; // danh sách lộ trình

}
