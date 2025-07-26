package com.example.warehousesystem.mapper;

import com.example.warehousesystem.dto.response.ScanItemResponse;
import com.example.warehousesystem.entity.*;

public class ScanItemMapper {

    public static ScanItemResponse toResponse(Item item, ExportOrderDetail detail, String message, boolean assigned) {
        SKU sku = item.getSku();
        Box box = item.getBox();
        Bin bin = box.getBin();

        ExportOrder exportOrder = detail != null ? detail.getExportOrder() : null;

        return ScanItemResponse.builder()
                .barcode(item.getBarcode())
                .skuCode(sku.getSkuCode())
                .skuName(sku.getName())
                .boxId(box.getId())
                .binCode(bin != null ? bin.getBinCode() : null)
                .itemStatus(item.getStatus().name())
                .assigned(assigned)
                .exportOrderId(exportOrder != null ? exportOrder.getId() : null)
                .exportOrderCode(exportOrder != null ? exportOrder.getOrderCode() : null)
                .detailId(detail != null ? detail.getId() : null)
                .message(message)
                .build();
    }
}
