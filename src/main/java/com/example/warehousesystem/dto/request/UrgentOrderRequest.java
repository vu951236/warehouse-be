package com.example.warehousesystem.dto.request;

import com.example.warehousesystem.entity.ExportOrder;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
//Ưu tiên đơn hàng gấp
public class UrgentOrderRequest {
    private Boolean urgent;
    private Integer maxResults;      // Giới hạn số đơn cần lấy
}
