package com.example.warehousesystem.dto.request;

import lombok.Data;

@Data
public class UpdateShelfRequest {
    private Integer id;            // ID của Shelf cần sửa
    private String shelfCode;
    private Integer binCount;
    private Integer warehouseId;   // ID kho chứa kệ hàng này
}
