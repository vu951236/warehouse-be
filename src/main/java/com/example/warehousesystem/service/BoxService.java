package com.example.warehousesystem.service;

import com.example.warehousesystem.dto.request.*;
import com.example.warehousesystem.dto.response.BoxResponse;
import com.example.warehousesystem.entity.Bin;
import com.example.warehousesystem.entity.Box;
import com.example.warehousesystem.entity.Item;
import com.example.warehousesystem.exception.AppException;
import com.example.warehousesystem.exception.ErrorCode;
import com.example.warehousesystem.mapper.BoxMapper;
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
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoxService {

    private final BinRepository binRepository;
    private final BoxRepository boxRepository;
    private final ItemRepository itemRepository;
    private final SKURepository skuRepository;

    @Transactional
    public BoxResponse createBox(CreateBoxRequest request) {
        // Tìm bin theo mã
        Bin bin = binRepository.findByBinCode(request.getBinCode())
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        // Tìm SKU theo mã
        var sku = skuRepository.findBySkuCode(request.getSkuCode())
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        // Kiểm tra input hợp lệ
        if (request.getCapacity() == null || request.getCapacity() < 0) {
            throw new AppException(ErrorCode.INVALID_INPUT);
        }

        // Lấy tổng sức chứa hiện tại của các box trong bin
        int totalUsedCapacity = binRepository.getUsedCapacityInBin(bin.getId());
        int binMaxCapacity = bin.getCapacity();

        // Kiểm tra còn chỗ không
        if (totalUsedCapacity + request.getCapacity() > binMaxCapacity) {
            throw new AppException(ErrorCode.INVALID_INPUT);
        }

        // Đếm số box hiện tại trong bin
        int count = boxRepository.countByBinId(bin.getId());

        // Tạo mã box theo quy tắc: SHELF-BIN-BX01
        String binCode = bin.getBinCode();
        String boxCode = String.format("%s-BX%02d", binCode, count + 1);

        // Tạo Box
        Box box = Box.builder()
                .boxCode(boxCode)
                .bin(bin)
                .sku(sku)
                .capacity(request.getCapacity())
                .usedCapacity(0)
                .isDeleted(false)
                .build();

        Box savedBox = boxRepository.save(box);
        return BoxMapper.toResponse(savedBox);
    }

    @Transactional
    public void deleteBox(String boxCode) {
        Box box = boxRepository.findByBoxCode(boxCode)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        Integer boxId = box.getId();

        // Lấy các item chưa bị xoá
        List<Item> items = itemRepository.findByBoxIdInAndIsDeletedFalse(Collections.singletonList(boxId));

        // Đánh dấu xoá item
        items.forEach(item -> item.setIsDeleted(true));
        itemRepository.saveAll(items);

        // Đánh dấu xoá box
        box.setIsDeleted(true);
        boxRepository.save(box);
    }

    /**
     * Tìm kiếm ngăn hàng theo các tiêu chí
     */
    public List<BoxResponse> searchBoxs(SearchBoxRequest request) {
        List<Box> boxs = boxRepository.searchBoxes(
                request.getBinCode(),
                request.getBoxCode(),
                request.getSkuCode(),
                request.getBarcode()
        );

        return boxs.stream()
                .map(BoxMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public BoxResponse updateBox(UpdateBoxRequest request) {
        // Tìm box theo mã hiện tại
        Box box = boxRepository.findByBoxCode(request.getBoxCode())
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        // Tìm bin mới theo binCode
        Bin newBin = binRepository.findByBinCode(request.getBinCode())
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        if (request.getCapacity() == null || request.getCapacity() < 0) {
            throw new AppException(ErrorCode.INVALID_INPUT);
        }

        if (request.getCapacity() < box.getUsedCapacity()) {
            throw new AppException(ErrorCode.INVALID_INPUT);
        }

        // Lấy tổng sức chứa hiện tại của các box trong bin
        int totalUsedCapacity = binRepository.getUsedCapacityInBin(newBin.getId());
        int binMaxCapacity = newBin.getCapacity();

        if (totalUsedCapacity + request.getCapacity() > binMaxCapacity) {
            throw new AppException(ErrorCode.INVALID_INPUT);
        }

        // Cập nhật bin nếu đổi bin
        if (!box.getBin().getId().equals(newBin.getId())) {
            box.setBin(newBin);

            // Đổi mã boxCode theo bin mới
            String binCode = newBin.getBinCode();
            String newBoxCode = String.format("%s-BX%02d", binCode, box.getId());
            box.setBoxCode(newBoxCode);
        }

        box.setCapacity(request.getCapacity());

        Box updatedBox = boxRepository.save(box);
        return BoxMapper.toResponse(updatedBox);
    }

    public byte[] exportBoxToPdf() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        // Tiêu đề
        Paragraph title = new Paragraph("📋 Danh Sách Các Box")
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
        table.addHeaderCell(new Cell().add(new Paragraph("Mã Box").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Số Item").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Mã Bin").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Mã Sku").setBold()));

        Map<Integer, Long> itemCountMap = itemRepository.countItemAvailableInAllBoxes()
                .stream()
                .collect(Collectors.toMap(
                        row -> (Integer) row[0],
                        row -> (Long) row[1]
                ));

        // Dữ liệu
        List<Box> boxs = boxRepository.findAllByIsDeletedFalse();
        for (Box box : boxs) {
            table.addCell(String.valueOf(box.getId()));
            table.addCell(box.getBoxCode());
            long itemCount = itemCountMap.getOrDefault(box.getId(), 0L);
            table.addCell(String.valueOf(itemCount));
            table.addCell(box.getBin() != null ? box.getBin().getBinCode() : "");
            table.addCell(box.getSku() != null ? box.getSku().getSkuCode() : "");
        }

        document.add(table);
        document.close();

        return outputStream.toByteArray();
    }
}