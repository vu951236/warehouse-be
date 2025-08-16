package com.example.warehousesystem.service;

import com.example.warehousesystem.dto.request.SearchBinRequest;
import com.example.warehousesystem.dto.request.UpdateBinRequest;
import com.example.warehousesystem.dto.response.AllBinResponse;
import com.example.warehousesystem.dto.response.BinDetailResponse;
import com.example.warehousesystem.dto.response.BinResponse;
import com.example.warehousesystem.entity.*;
import com.example.warehousesystem.exception.AppException;
import com.example.warehousesystem.exception.ErrorCode;
import com.example.warehousesystem.mapper.AllBinMapper;
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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BinService {

    private final ShelfRepository shelfRepository;
    private final BinRepository binRepository;
    private final BoxRepository boxRepository;
    private final ItemRepository itemRepository;
    private final SKURepository skuRepository;

    @Transactional
    public void deleteBin(String binCode) {
        Bin bin = binRepository.findByBinCode(binCode)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

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

        // Trừ binCount trong Shelf
        Shelf shelf = bin.getShelf();
        if (shelf != null && shelf.getBinCount() != null && shelf.getBinCount() > 0) {
            shelf.setBinCount(shelf.getBinCount() - 1);
            shelfRepository.save(shelf);
        }

        // Đánh dấu xoá bin
        bin.setIsDeleted(true);
        binRepository.save(bin);
    }

    @Transactional
    public BinResponse updateBin(UpdateBinRequest request) {
        Bin bin = binRepository.findByBinCode(request.getBinCode())
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        bin.setCapacity(request.getCapacity());
        Bin updatedBin = binRepository.save(bin);

        return BinMapper.toResponse(updatedBin);
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

        Map<Integer, Long> boxCountMap = boxRepository.countBoxesAvailableInAllBins()
                .stream()
                .collect(Collectors.toMap(
                        row -> (Integer) row[0],
                        row -> (Long) row[1]
                ));

        Map<Integer, String> skuCodeMap = skuRepository.findSkuCodesByBinId()
                .stream()
                .collect(Collectors.toMap(
                        row -> (Integer) row[0],
                        row -> (String) row[1]
                ));

        List<Bin> bins = binRepository.findAllByIsDeletedFalse();
        for (Bin bin : bins) {
            table.addCell(String.valueOf(bin.getId()));
            table.addCell(bin.getBinCode());

            long boxCount = boxCountMap.getOrDefault(bin.getId(), 0L);
            table.addCell(String.valueOf(boxCount));

            table.addCell(bin.getShelf() != null ? bin.getShelf().getShelfCode() : "");

            String skuCode = skuCodeMap.getOrDefault(bin.getId(), "");
            table.addCell(skuCode);
        }

        document.add(table);
        document.close();

        return outputStream.toByteArray();
    }

    public List<AllBinResponse> getBinsByShelfId(Integer shelfId) {
        List<Bin> bins = binRepository.findByShelfIdAndIsDeletedFalse(shelfId);

        return bins.stream()
                .map(bin -> {
                    Long itemCount = itemRepository.countItemsByBinId(bin.getId());
                    return AllBinMapper.toResponse(bin, itemCount);
                })
                .toList();
    }

    public BinDetailResponse getBinDetail(Integer binId) {
        Bin bin = binRepository.findById(binId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        // Tổng số item trong bin
        Long itemCount = itemRepository.countItemsByBinId(bin.getId());

        // Lấy tất cả box thuộc bin
        List<Box> boxes = boxRepository.findByBinIdInAndIsDeletedFalse(Collections.singletonList(binId));

        int totalUsedCapacity = boxes.stream()
                .mapToInt(Box::getUsedCapacity)
                .sum();

        // Tính tỉ lệ chứa
        double utilizationRate = bin.getCapacity() == 0 ? 0.0 :
                (double) totalUsedCapacity / bin.getCapacity() * 100.0;

        return BinDetailResponse.builder()
                .id(bin.getId())
                .binCode(bin.getBinCode())
                .itemCount(itemCount)
                .utilizationRate(utilizationRate)
                .build();
    }



}