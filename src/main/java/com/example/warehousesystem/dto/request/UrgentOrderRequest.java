package com.example.warehousesystem.dto.request;

import com.example.warehousesystem.entity.ExportOrder;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
//Ưu tiên đơn hàng gấp
public class UrgentOrderRequest {
    private ExportOrder.Status status;     // draft, confirmed, cancelled
    private Integer maxResults;      // Giới hạn số đơn cần lấy
}
