package com.example.warehousesystem.controller;

import com.example.warehousesystem.dto.request.CreateBoxRequest;
import com.example.warehousesystem.dto.request.SearchBoxRequest;
import com.example.warehousesystem.dto.request.UpdateBoxRequest;
import com.example.warehousesystem.dto.response.BoxResponse;
import com.example.warehousesystem.service.BoxService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boxes")
@RequiredArgsConstructor
public class BoxController {

    private final BoxService boxService;

    /**
     * Tạo mới box
     */
    @PostMapping("/create")
    public ResponseEntity<BoxResponse> createBox(@RequestBody CreateBoxRequest request) {
        BoxResponse response = boxService.createBox(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Cập nhật box
     */
    @PutMapping("/update")
    public ResponseEntity<BoxResponse> updateBox(@RequestBody UpdateBoxRequest request) {
        BoxResponse response = boxService.updateBox(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Xoá mềm box theo boxCode
     */
    @DeleteMapping("/{boxCode}")
    public ResponseEntity<Void> deleteBox(@PathVariable String boxCode) {
        boxService.deleteBox(boxCode);
        return ResponseEntity.noContent().build();
    }

    /**
     * Tìm kiếm các box theo điều kiện
     */
    @PostMapping("/search")
    public ResponseEntity<List<BoxResponse>> searchBoxes(@RequestBody SearchBoxRequest request) {
        List<BoxResponse> responses = boxService.searchBoxs(request);
        return ResponseEntity.ok(responses);
    }

    /**
     * Xuất PDF danh sách box
     */
    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportBoxesToPdf() {
        byte[] pdfBytes = boxService.exportBoxToPdf();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=box_list.pdf")
                .body(pdfBytes);
    }
}