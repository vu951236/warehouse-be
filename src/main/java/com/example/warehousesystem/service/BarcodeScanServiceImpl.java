package com.example.warehousesystem.service;

import com.example.warehousesystem.dto.request.ScanBarcodeRequest;
import com.example.warehousesystem.dto.response.ScanBarcodeResponse;
import com.example.warehousesystem.entity.*;
import com.example.warehousesystem.exception.AppException;
import com.example.warehousesystem.exception.ErrorCode;
import com.example.warehousesystem.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BarcodeScanServiceImpl implements BarcodeScanService {

    private final ItemRepository itemRepository;

    @Override
    public ScanBarcodeResponse scanBarcode(ScanBarcodeRequest request) {
        String barcode = request.getBarcode();
        Item item = itemRepository.findByBarcode(barcode)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Item not found for barcode: " + barcode));

        SKU sku = item.getSku();
        Box box = item.getBox();
        Bin bin = box.getBin();
        Shelf shelf = bin.getShelf();
        Warehouse warehouse = shelf.getWarehouse();

        return ScanBarcodeResponse.builder()
                .skuCode(sku.getSkuCode())
                .itemId(item.getId())
                .type(sku.getType())
                .color(sku.getColor())
                .size(sku.getSize())
                .boxCode(box.getBoxCode())
                .binCode(bin.getBinCode())
                .shelfCode(shelf.getShelfCode())
                .warehouse(warehouse.getName())
                .status(item.getStatus().name())
                .build();
    }
}
