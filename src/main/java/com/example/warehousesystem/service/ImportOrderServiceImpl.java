package com.example.warehousesystem.service;

import com.example.warehousesystem.dto.ExcelItemDTO;
import com.example.warehousesystem.dto.ScanBarcodeDTO;
import com.example.warehousesystem.dto.request.ImportExcelItemRequest;
import com.example.warehousesystem.dto.request.ImportOrderSearchRequest;
import com.example.warehousesystem.dto.request.ImportScanBarcodeRequest;
import com.example.warehousesystem.dto.request.SearchImportBySKURequest;
import com.example.warehousesystem.dto.response.ImportItemsResponse;
import com.example.warehousesystem.dto.response.ImportOrderResponse;
import com.example.warehousesystem.dto.response.SearchImportBySKUResponse;
import com.example.warehousesystem.entity.*;
import com.example.warehousesystem.exception.AppException;
import com.example.warehousesystem.exception.ErrorCode;
import com.example.warehousesystem.logic.PutAwayOptimizer;
import com.example.warehousesystem.mapper.ImportOrderMapper;
import com.example.warehousesystem.mapper.ItemImportMapper;
import com.example.warehousesystem.mapper.SearchImportBySKUMapper;
import com.example.warehousesystem.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImportOrderServiceImpl implements ImportOrderService {

    private final ImportOrderRepository importOrderRepository;
    private final ImportOrderMapper importOrderMapper;

    private final UserRepository userRepository;
    private final SKURepository skuRepository;
    private final BinRepository binRepository;
    private final BoxRepository boxRepository;
    private final ItemRepository itemRepository;
    private final ImportOrderDetailRepository importOrderDetailRepository;
    private final PutAwayOptimizer putAwayOptimizer;


    @Override
    public List<ImportOrderResponse> searchImportOrders(ImportOrderSearchRequest request) {
        List<ImportOrder> orders = importOrderRepository.searchImportOrders(
                request.getSource(),
                request.getStatus(),
                request.getCreatedBy(),
                request.getStartDate() != null ? request.getStartDate().atStartOfDay() : null,
                request.getEndDate() != null ? request.getEndDate().atTime(23, 59, 59) : null
        );
        return orders.stream()
                .map(importOrderMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ImportItemsResponse importSingleItem(ImportScanBarcodeRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        ImportOrder order = importOrderRepository.save(
                ItemImportMapper.toImportOrder(request.getSource(), request.getNote(), user)
        );

        List<ImportItemsResponse.ImportedItemInfo> results = new ArrayList<>();

        for (ScanBarcodeDTO dto : request.getScannedItems()) {
            SKU sku = skuRepository.findBySkuCode(dto.getSkuCode())
                    .orElseThrow(() -> new AppException(ErrorCode.SKU_NOT_FOUND, "SKU not found"));

            results.addAll(handleImportItem(order, sku.getId(), dto.getQuantity()));
        }


        return ImportItemsResponse.builder()
                .importOrderId(order.getId())
                .importedItems(results)
                .build();
    }

    @Override
    @Transactional
    public ImportItemsResponse importFromExcel(ImportExcelItemRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        ImportOrder order = importOrderRepository.save(
                ItemImportMapper.toImportOrder(request.getSource(), request.getNote(), user)
        );

        List<ImportItemsResponse.ImportedItemInfo> results = new ArrayList<>();
        for (ExcelItemDTO dto : request.getItems()) {
            SKU sku = skuRepository.findBySkuCode(dto.getSkuCode())
                    .orElseThrow(() -> new AppException(ErrorCode.SKU_NOT_FOUND, "SKU not found"));

            for (int i = 0; i < dto.getQuantity(); i++) {
                results.addAll(handleImportItem(order, sku.getId(), 1));
            }
        }

        return ImportItemsResponse.builder()
                .importOrderId(order.getId())
                .importedItems(results)
                .build();
    }

    private List<ImportItemsResponse.ImportedItemInfo> handleImportItem(ImportOrder order, Integer skuId, Integer quantity) {
        SKU sku = skuRepository.findById(skuId)
                .orElseThrow(() -> new RuntimeException("SKU not found"));

        int requiredVolume = (int) (sku.getUnitVolume() * quantity);

        Box box = putAwayOptimizer.findOptimalBox(sku, quantity);
        if (box.getId() == null) {
            box = boxRepository.save(box);
        }

        Bin bin = box.getBin();

        ImportOrderDetail detail = ItemImportMapper.toDetail(order, sku, quantity);
        importOrderDetailRepository.save(detail);

        int existingItemCount = itemRepository.countBySkuId(sku.getId());

        List<ImportItemsResponse.ImportedItemInfo> itemInfos = new ArrayList<>();
        for (int i = 1; i <= quantity; i++) {
            // Tăng index tiếp theo
            int index = existingItemCount + i;
            String barcode = sku.getSkuCode() + "-" + String.format("%05d", index);

            Item item = ItemImportMapper.toItem(box, sku);
            item.setBarcode(barcode);

            item = itemRepository.save(item);

            itemInfos.add(new ImportItemsResponse.ImportedItemInfo(barcode, item.getId(), box.getId(), bin.getId()));
        }

        box.setUsedCapacity(box.getUsedCapacity() + requiredVolume);
        boxRepository.save(box);

        return itemInfos;
    }


    @Override
    public ByteArrayOutputStream generateImportTemplate() throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("ImportTemplate");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("barcode");
        header.createCell(1).setCellValue("skuId");
        header.createCell(2).setCellValue("quantity");

        Row exampleRow = sheet.createRow(1);
        exampleRow.createCell(0).setCellValue("ABC123456789");
        exampleRow.createCell(1).setCellValue(1);
        exampleRow.createCell(2).setCellValue(100);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return out;
    }

    @Override
    @Transactional
    public ImportItemsResponse importByBarcode(ImportScanBarcodeRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "No Bin with available capacity"));

        ImportOrder order = importOrderRepository.save(
                ItemImportMapper.toImportOrder(request.getSource(), request.getNote(), user)
        );

        List<ImportItemsResponse.ImportedItemInfo> result = new ArrayList<>();

        for (ScanBarcodeDTO dto : request.getScannedItems()) {

            SKU sku = skuRepository.findBySkuCode(dto.getSkuCode())
                    .orElseThrow(() -> new AppException(ErrorCode.SKU_NOT_FOUND, "No Bin with available capacity"));

            int requiredVolume = (int) (sku.getUnitVolume() * dto.getQuantity());

            Bin bin = binRepository.findBinsWithAvailableCapacity().stream()
                    .filter(b -> {
                        int used = binRepository.getUsedCapacityInBin(b.getId());
                        return b.getCapacity() - used >= requiredVolume;
                    })
                    .findFirst()
                    .orElseThrow(() -> new AppException(ErrorCode.NO_BIN_CAPACITY, "No Bin with available capacity"));

            Box box = boxRepository.findAvailableBoxes(sku.getId(), requiredVolume).stream()
                    .filter(b -> b.getBin().getId().equals(bin.getId()))
                    .findFirst()
                    .orElseGet(() -> {
                        int count = boxRepository.countBoxesInBin(bin.getId()) + 1;
                        String binCode = bin.getBinCode(); // VD: SH01-B01
                        String boxCode = binCode + "-BX" + String.format("%02d", count); // SH01-B01-BX01

                        Box newBox = Box.builder()
                                .sku(sku)
                                .bin(bin)
                                .capacity(1000)
                                .usedCapacity(0)
                                .boxCode(boxCode)
                                .isDeleted(false)
                                .build();

                        return boxRepository.save(newBox);
                    });

            int existingItemCount = itemRepository.countBySkuId(sku.getId());

            for (int i = 1; i <= dto.getQuantity(); i++) {
                int index = existingItemCount + i;
                String barcode = sku.getSkuCode() + "-" + String.format("%05d", index);

                Item item = ItemImportMapper.toItem(box, sku);
                item.setBarcode(barcode);
                item = itemRepository.save(item);

                result.add(new ImportItemsResponse.ImportedItemInfo(
                        item.getBarcode(), item.getId(), box.getId(), bin.getId()
                ));
            }

            importOrderDetailRepository.save(ItemImportMapper.toDetail(order, sku, dto.getQuantity()));
            box.setUsedCapacity(box.getUsedCapacity() + requiredVolume);
            boxRepository.save(box);
        }

        return ImportItemsResponse.builder()
                .importOrderId(order.getId())
                .importedItems(result)
                .build();
    }

    @Override
    public List<SearchImportBySKUResponse> getImportHistoryBySKU(SearchImportBySKURequest request) {
        LocalDateTime from = request.getFromDate() != null ? LocalDate.parse(request.getFromDate()).atStartOfDay() : null;
        LocalDateTime to = request.getToDate() != null ? LocalDate.parse(request.getToDate()).atTime(23, 59, 59) : null;

        List<Object[]> results = importOrderDetailRepository.findImportDetailsBySku(
                request.getSkuCode(), from, to
        );

        return results.stream()
                .map(row -> {
                    var detail = (com.example.warehousesystem.entity.ImportOrderDetail) row[0];
                    var order = (com.example.warehousesystem.entity.ImportOrder) row[1];
                    var warehouseName = (String) row[2];
                    return SearchImportBySKUMapper.toImportBySkuResponse(order, detail, warehouseName);
                })
                .toList();
    }


}