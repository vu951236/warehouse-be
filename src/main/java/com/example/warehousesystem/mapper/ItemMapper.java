package com.example.warehousesystem.mapper;

import com.example.warehousesystem.dto.response.ImportItemResponse;
import com.example.warehousesystem.entity.Bin;
import com.example.warehousesystem.entity.Box;
import com.example.warehousesystem.entity.Item;
import com.example.warehousesystem.entity.Shelf;

public class ItemMapper {
    public static ImportItemResponse toResponse(Item item) {
        Box box = item.getBox();
        Bin bin = box.getBin();
        Shelf shelf = bin.getShelf();
        return ImportItemResponse.builder()
                .itemId(item.getId())
                .barcode(item.getBarcode())
                .boxId(box.getId())
                .boxCode(String.valueOf(box.getId()))
                .skuCode(item.getSku().getSkuCode())
                .shelfCode(shelf.getShelfCode())
                .binCode(bin.getBinCode())
                .build();
    }
}

