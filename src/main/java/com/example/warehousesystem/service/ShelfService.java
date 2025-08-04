package com.example.warehousesystem.service;

import com.example.warehousesystem.dto.request.CreateShelfRequest;
import com.example.warehousesystem.dto.request.SearchShelfRequest;
import com.example.warehousesystem.dto.response.BinResponse;
import com.example.warehousesystem.dto.response.BoxResponse;
import com.example.warehousesystem.dto.response.ShelfResponse;
import com.example.warehousesystem.entity.*;
import com.example.warehousesystem.exception.AppException;
import com.example.warehousesystem.exception.ErrorCode;
import com.example.warehousesystem.mapper.BinMapper;
import com.example.warehousesystem.mapper.BoxMapper;
import com.example.warehousesystem.mapper.CreateShelfMapper;
import com.example.warehousesystem.mapper.ShelfMapper;
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
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Shelf not found with code: " + shelfCode));

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
    public ShelfResponse createShelf(CreateShelfRequest request) {
        // Kiểm tra trùng mã kệ
        if (shelfRepository.existsByShelfCode(request.getShelfCode())) {
            throw new IllegalArgumentException("Mã kệ đã tồn tại: " + request.getShelfCode());
        }

        // Lấy warehouse theo ID
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new AppException(ErrorCode.WAREHOUSE_NOT_FOUND, "No Bin with available capacity"));

        // Tạo đối tượng Shelf
        Shelf shelf = CreateShelfMapper.toEntity(request, warehouse);
        shelf.setIsDeleted(false);

        // Lưu kệ để có ID
        Shelf savedShelf = shelfRepository.save(shelf);

        // Tạo 16 bin theo quy tắc mã
        List<Bin> binList = new ArrayList<>();
        for (int i = 1; i <= 16; i++) {
            String binCode = request.getShelfCode() + "-B" + String.format("%02d", i);  // SH01-B01, SH01-B02,...
            Bin bin = Bin.builder()
                    .binCode(binCode)
                    .capacity(100)
                    .shelf(savedShelf)
                    .isDeleted(false)
                    .build();
            binList.add(bin);
        }

        binRepository.saveAll(binList);

        return CreateShelfMapper.toResponse(savedShelf);
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

    public List<ShelfResponse> getAllShelves() {
        List<Shelf> shelves = shelfRepository.findAllByIsDeletedFalse();
        return shelves.stream()
                .map(ShelfMapper::toResponse)
                .toList();
    }

    public List<BinResponse> getBinsByShelfId(Integer shelfId) {
        List<Bin> bins = binRepository.findByShelfIdAndIsDeletedFalse(shelfId);
        return bins.stream()
                .map(BinMapper::toResponse)
                .toList();
    }

    public List<BoxResponse> getBoxesByBinId(Integer binId) {
        return boxRepository.findByBinIdInAndIsDeletedFalse(Collections.singletonList(binId)).stream()
                .map(BoxMapper::toResponse)
                .toList();
    }

}