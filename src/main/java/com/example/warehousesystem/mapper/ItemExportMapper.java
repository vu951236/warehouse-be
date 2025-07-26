package com.example.warehousesystem.mapper;

import com.example.warehousesystem.dto.response.ExportItemResponse;
import com.example.warehousesystem.entity.Item;
import com.example.warehousesystem.entity.Box;
import com.example.warehousesystem.entity.Bin;
import com.example.warehousesystem.entity.Shelf;

public class ItemExportMapper {

    public static ExportItemResponse toResponse(Item item) {
        Box box = item.getBox();
        Bin bin = box.getBin();
        Shelf shelf = bin.getShelf();

        return ExportItemResponse.builder()
                .itemId(item.getId())
                .barcode(item.getBarcode())
                .skuCode(item.getSku().getSkuCode())
                .boxId(box.getId())
                .boxCode(String.valueOf(box.getId()))
                .binCode(bin.getBinCode())
                .shelfCode(shelf.getShelfCode())
                .build();
    }
}
