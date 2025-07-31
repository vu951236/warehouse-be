package com.example.warehousesystem.dto.response;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImportItemsResponse {
    private Integer importOrderId;
    private List<ImportedItemInfo> importedItems;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ImportedItemInfo {
        private String barcode;
        private Integer itemId;
        private Integer boxId;
        private Integer binId;
    }
}



