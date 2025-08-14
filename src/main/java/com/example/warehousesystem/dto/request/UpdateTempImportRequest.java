package com.example.warehousesystem.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateTempImportRequest {
    private Long id;          // id bản ghi tạm
    private Integer quantity; // số lượng mới (>=1)
    private String note;      // ghi chú
    private String skuCode;   // cho phép đổi SKU (optional)
    private String source;    // nguồn (optional) nếu bạn đang lưu ở bảng tạm
}
