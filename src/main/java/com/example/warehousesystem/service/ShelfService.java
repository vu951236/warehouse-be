package com.example.warehousesystem.service;

import com.example.warehousesystem.dto.request.CreateShelfRequest;
import com.example.warehousesystem.dto.request.SearchShelfRequest;
import com.example.warehousesystem.dto.response.*;
import com.example.warehousesystem.entity.*;
import com.example.warehousesystem.exception.AppException;
import com.example.warehousesystem.exception.ErrorCode;
import com.example.warehousesystem.mapper.*;
import com.example.warehousesystem.repository.BinRepository;
import com.example.warehousesystem.repository.ShelfRepository;
import com.example.warehousesystem.repository.WarehouseRepository;
import com.example.warehousesystem.repository.BoxRepository;
import com.example.warehousesystem.repository.ItemRepository;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShelfService {

    private final ShelfRepository shelfRepository;
    private final WarehouseRepository warehouseRepository;
    private final BinRepository binRepository;
    private final BoxRepository boxRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public void deleteShelf(String shelfCode) {
        Shelf shelf = shelfRepository.findByShelfCode(shelfCode)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        Integer shelfId = shelf.getId();

        // Lấy các bin chưa bị xoá
        List<Bin> bins = binRepository.findByShelfIdAndIsDeletedFalse(shelfId);

        // Lấy tất cả binId
        List<Integer> binIds = bins.stream().map(Bin::getId).toList();

        // Lấy tất cả box theo binIds
        List<Box> boxes = boxRepository.findByBinIdInAndIsDeletedFalse(binIds);
        List<Integer> boxIds = boxes.stream().map(Box::getId).toList();

        // Lấy tất cả item theo boxIds
        List<Item> items = itemRepository.findByBoxIdInAndIsDeletedFalse(boxIds);

        // Đánh dấu xoá item
        items.forEach(item -> item.setIsDeleted(true));
        itemRepository.saveAll(items);

        // Đánh dấu xoá box
        boxes.forEach(box -> box.setIsDeleted(true));
        boxRepository.saveAll(boxes);

        // Đánh dấu xoá bin
        bins.forEach(bin -> bin.setIsDeleted(true));
        binRepository.saveAll(bins);

        // Đánh dấu xoá shelf
        shelf.setIsDeleted(true);
        shelfRepository.save(shelf);
    }

    /**
     * Tìm kiếm kệ hàng theo các tiêu chí
     */
    public List<ShelfResponse> searchShelves(SearchShelfRequest request) {
        List<Shelf> shelves = shelfRepository.searchShelves(
                request.getShelfCode(),
                request.getWarehouseId(),
                request.getBinCode(),
                request.getBoxCode(),
                request.getSkuCode()
        );

        return shelves.stream()
                .map(ShelfMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Tạo mới một kệ hàng cùng với 16 bin con
     */
    @Transactional
    public ShelfResponse createShelf() {
        Warehouse warehouse = warehouseRepository.findById(1)
                .orElseThrow(() -> new AppException(ErrorCode.WAREHOUSE_NOT_FOUND));

        Shelf shelf = Shelf.builder()
                .binCount(16)
                .warehouse(warehouse)
                .isDeleted(false)
                .shelfCode("TEMP")
                .build();

        Shelf savedShelf = shelfRepository.save(shelf);

        String shelfCode = "SH" + String.format("%02d", savedShelf.getId());
        savedShelf.setShelfCode(shelfCode);

        savedShelf = shelfRepository.save(savedShelf);

        List<Bin> binList = new ArrayList<>();
        List<String> binCodes = new ArrayList<>();

        for (int i = 1; i <= 16; i++) {
            String binCode = shelfCode + "-B" + String.format("%02d", i);
            Bin bin = Bin.builder()
                    .binCode(binCode)
                    .capacity(600)
                    .shelf(savedShelf)
                    .isDeleted(false)
                    .build();
            binList.add(bin);
            binCodes.add(binCode);
        }

        binRepository.saveAll(binList);

        return CreateShelfMapper.toResponse(savedShelf, binCodes);
    }

    public byte[] exportShelvesToPdf() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        // Tiêu đề
        Paragraph title = new Paragraph("📋 Danh Sách Kệ Hàng")
                .setBold()
                .setFontSize(16)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(title);

        // Bảng dữ liệu
        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 3, 2, 2, 3}))
                .setWidth(UnitValue.createPercentValue(100));

        // Header
        table.addHeaderCell(new Cell().add(new Paragraph("ID").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Mã Kệ").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Số Bin").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("ID Kho").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Tên Kho").setBold()));

        // Dữ liệu
        List<Shelf> shelves = shelfRepository.findAllByIsDeletedFalse();
        for (Shelf shelf : shelves) {
            table.addCell(String.valueOf(shelf.getId()));
            table.addCell(shelf.getShelfCode());
            table.addCell(String.valueOf(shelf.getBinCount()));
            table.addCell(String.valueOf(shelf.getWarehouse().getId()));
            table.addCell(shelf.getWarehouse().getName());
        }

        document.add(table);
        document.close();

        return outputStream.toByteArray();
    }

    public List<AllShelfResponse> getAllShelves() {
        List<Shelf> shelves = shelfRepository.findAllByIsDeletedFalse();

        return shelves.stream()
                .map(shelf -> {
                    Long itemCount = itemRepository.countItemsByShelfId(shelf.getId());
                    return AllShelfMapper.toResponse(shelf, itemCount);
                })
                .toList();
    }



    public ShelfDetailResponse getShelfDetail(Integer shelfId) {
        Shelf shelf = shelfRepository.findById(shelfId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        // Tổng số item trong shelf
        Long itemCount = itemRepository.countItemsByShelfId(shelf.getId());

        // Lấy tất cả bin thuộc shelf
        List<Bin> bins = binRepository.findByShelfIdAndIsDeletedFalse(shelfId);
        int totalBinCapacity = bins.stream()
                .mapToInt(Bin::getCapacity)
                .sum();

        // Lấy tất cả box thuộc shelf thông qua bin
        List<Integer> binIds = bins.stream().map(Bin::getId).toList();
        List<Box> boxes = boxRepository.findByBinIdInAndIsDeletedFalse(binIds);

        int totalUsedCapacity = boxes.stream()
                .mapToInt(Box::getUsedCapacity)
                .sum();

        // Tính tỉ lệ chứa
        double utilizationRate = totalBinCapacity == 0 ? 0.0 :
                (double) totalUsedCapacity / totalBinCapacity * 100.0;

        return ShelfDetailResponse.builder()
                .id(shelf.getId())
                .shelfCode(shelf.getShelfCode())
                .itemCount(itemCount)
                .utilizationRate(utilizationRate)
                .build();
    }


}