package com.example.warehousesystem.controller;

import com.example.warehousesystem.dto.request.CreateShelfRequest;
import com.example.warehousesystem.dto.request.DeleteShelfRequest;
import com.example.warehousesystem.dto.request.SearchShelfRequest;
import com.example.warehousesystem.dto.response.BinResponse;
import com.example.warehousesystem.dto.response.BoxResponse;
import com.example.warehousesystem.dto.response.ShelfResponse;
import com.example.warehousesystem.entity.Bin;
import com.example.warehousesystem.entity.Box;
import com.example.warehousesystem.entity.Shelf;
import com.example.warehousesystem.service.ShelfService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/shelves")
@RequiredArgsConstructor
public class ShelfController {

    private final ShelfService shelfService;

    @PostMapping("/search")
    public List<ShelfResponse> searchShelves(@RequestBody SearchShelfRequest request) {
        return shelfService.searchShelves(request);
    }

    @PostMapping("/create")
    public ResponseEntity<ShelfResponse> createShelf(@Valid @RequestBody CreateShelfRequest request) {
        ShelfResponse response = shelfService.createShelf(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteShelf(@RequestBody DeleteShelfRequest request) {
        shelfService.deleteShelf(request.getShelfCode());
        return ResponseEntity.ok("Xóa kệ hàng thành công");
    }

    @GetMapping("/pdf")
    public void downloadShelvesPdf(HttpServletResponse response) throws IOException {
        byte[] pdfContent = shelfService.exportShelvesToPdf();
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=shelves.pdf");
        response.getOutputStream().write(pdfContent);
        response.getOutputStream().flush();
    }

    @GetMapping("/{shelfId}/bins")
    public ResponseEntity<List<BinResponse>> getBinsByShelf(@PathVariable Integer shelfId) {
        List<BinResponse> bins = shelfService.getBinsByShelfId(shelfId);
        return ResponseEntity.ok(bins);}

    @GetMapping("/bin/{binId}/boxes")
    public ResponseEntity<List<BoxResponse>> getBoxesByBin(@PathVariable Integer binId) {
        List<BoxResponse> boxes = shelfService.getBoxesByBinId(binId);
        return ResponseEntity.ok(boxes);}

    @GetMapping("/all")
    public ResponseEntity<List<ShelfResponse>> getAllShelves() {
        List<ShelfResponse> shelves = shelfService.getAllShelves();
        return ResponseEntity.ok(shelves);
    }

}
