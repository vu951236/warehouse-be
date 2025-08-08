package com.example.warehousesystem.service;

import com.example.warehousesystem.dto.request.*;
import com.example.warehousesystem.dto.response.ItemResponse;
import com.example.warehousesystem.entity.Box;
import com.example.warehousesystem.entity.Item;
import com.example.warehousesystem.exception.AppException;
import com.example.warehousesystem.exception.ErrorCode;
import com.example.warehousesystem.mapper.ItemMapper;
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
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final BoxRepository boxRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public void deleteItem(String itemCode) {
        Item item = itemRepository.findByBarcode(itemCode)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        if (item.getIsDeleted()) {
            // Nếu đã bị xoá rồi thì không làm gì nữa
            return;
        }

        // Đánh dấu xoá item
        item.setIsDeleted(true);
        itemRepository.save(item);

        // Cập nhật used_capacity trong box
        Box box = item.getBox();
        Float unitVolume = item.getSku().getUnitVolume();

        if (unitVolume != null && unitVolume > 0) {
            int newUsedCapacity = (int) Math.max(0, box.getUsedCapacity() - unitVolume); // Không để âm
            box.setUsedCapacity(newUsedCapacity);
            boxRepository.save(box);
        }
    }

    /**
     * Tìm kiếm ngăn hàng theo các tiêu chí
     */
    public List<ItemResponse> searchItems(SearchItemRequest request) {
        List<Item> items = itemRepository.searchItems(
                request.getBarcode(),
                request.getBoxCode(),
                request.getSkuCode(),
                request.getStatus(),
                request.getExportCode(),
                request.getImportCode()
        );

        return items.stream()
                .map(ItemMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ItemResponse updateItem(UpdateItemRequest request) {
        // Tìm item theo barcode
        Item item = itemRepository.findByBarcode(request.getBarcode())
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        // Tìm box mới theo boxCode
        Box newBox = boxRepository.findByBoxCode(request.getBoxCode())
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        // Box cũ và đơn vị thể tích
        Box oldBox = item.getBox();
        Float unitVolume = item.getSku().getUnitVolume();

        // Nếu box được chọn khác box hiện tại → cập nhật usedCapacity
        if (!oldBox.getId().equals(newBox.getId())) {
            Integer oldUsedCapacity = oldBox.getUsedCapacity() != null ? oldBox.getUsedCapacity() : 0;
            Integer newUsedCapacity = newBox.getUsedCapacity() != null ? newBox.getUsedCapacity() : 0;

            if (unitVolume != null && unitVolume > 0) {
                int updatedOldUsedCapacity = (int) Math.max(0, oldUsedCapacity - unitVolume);
                oldBox.setUsedCapacity(updatedOldUsedCapacity);
                boxRepository.save(oldBox);

                int updatedNewUsedCapacity = (int) (newUsedCapacity + unitVolume);
                newBox.setUsedCapacity(updatedNewUsedCapacity);
                boxRepository.save(newBox);
            }

            // Cập nhật lại box cho item
            item.setBox(newBox);
        }

        // Cập nhật lại trạng thái cho item
        item.setStatus(request.getStatus());

        Item updatedItem = itemRepository.save(item);
        return ItemMapper.toResponse(updatedItem);
    }

    public byte[] exportItemToPdf() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        // Tiêu đề
        Paragraph title = new Paragraph("📋 Danh Sách Các Item")
                .setBold()
                .setFontSize(16)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(title);

        // Bảng dữ liệu
        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 3, 3, 3, 3, 3, 3}))
                .setWidth(UnitValue.createPercentValue(100));

        // Header
        table.addHeaderCell(new Cell().add(new Paragraph("ID").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Mã Item").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Mã SKU").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Tên SKU").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Mã Box").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Trạng Thái").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Ngày tạo").setBold()));

        List<Item> items = itemRepository.findAllByIsDeletedFalse();

        for (Item item : items) {
            table.addCell(String.valueOf(item.getId()));
            table.addCell(item.getBarcode());
            table.addCell(item.getSku() != null ? item.getSku().getSkuCode() : "");
            table.addCell(item.getSku() != null ? item.getSku().getName() : "");
            table.addCell(item.getBox() != null ? item.getBox().getBoxCode() : "");
            table.addCell(item.getStatus() != null ? item.getStatus().name() : "");
            table.addCell(item.getCreatedAt() != null ? item.getCreatedAt().toString() : "");
        }

        document.add(table);
        document.close();
        return outputStream.toByteArray();
    }
}