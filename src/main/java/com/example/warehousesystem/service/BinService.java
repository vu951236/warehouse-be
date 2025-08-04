package com.example.warehousesystem.service;

import com.example.warehousesystem.dto.request.SearchBinRequest;
import com.example.warehousesystem.dto.response.BinResponse;
import com.example.warehousesystem.entity.*;
import com.example.warehousesystem.exception.AppException;
import com.example.warehousesystem.exception.ErrorCode;
import com.example.warehousesystem.mapper.BinMapper;
import com.example.warehousesystem.repository.*;
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
public class BinService {

    private final ShelfRepository shelfRepository;
    private final WarehouseRepository warehouseRepository;
    private final BinRepository binRepository;
    private final BoxRepository boxRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public void deleteBin(String binCode) {
        Bin bin = binRepository.findByBinCode(binCode)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Shelf not found with code: " + binCode));

        Integer binId = bin.getId();

        // Lấy các box chưa bị xoá
        List<Box> boxs = boxRepository.findByBinIdInAndIsDeletedFalse(Collections.singletonList(binId));

        // Lấy tất cả boxId
        List<Integer> boxIds = boxs.stream().map(Box::getId).toList();

        // Lấy tất cả item theo boxIds
        List<Item> items = itemRepository.findByBoxIdInAndIsDeletedFalse(boxIds);
        List<Integer> itemIds = items.stream().map(Item::getId).toList();

        // Đánh dấu xoá item
        items.forEach(item -> item.setIsDeleted(true));
        itemRepository.saveAll(items);

        // Đánh dấu xoá box
        boxs.forEach(box -> box.setIsDeleted(true));
        boxRepository.saveAll(boxs);

        // Đánh dấu xoá bin
        bin.setIsDeleted(true);
        binRepository.save(bin);
    }

    /**
     * Tìm kiếm ngăn hàng theo các tiêu chí
     */
    public List<BinResponse> searchBins(SearchBinRequest request) {
        List<Bin> bins = binRepository.searchBins(
                request.getBinCode(),
                request.getShelfCode(),
                request.getBoxCode(),
                request.getSkuCode()
        );

        return bins.stream()
                .map(BinMapper::toResponse)
                .collect(Collectors.toList());
    }

    public byte[] exportBinToPdf() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        // Tiêu đề
        Paragraph title = new Paragraph("📋 Danh Sách Ngăn Hàng")
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
        table.addHeaderCell(new Cell().add(new Paragraph("Mã Bin").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Số Box").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Mã Shelf").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Mã Sku").setBold()));

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


}