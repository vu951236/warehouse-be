package com.example.warehousesystem.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.List;

import com.example.warehousesystem.entity.Clothing;
import com.example.warehousesystem.service.ClothingService;
import com.example.warehousesystem.dto.request.ClothingDTO;

@RestController
@RequestMapping("/api/clothing")
public class ClothingController {
    @Autowired
    private ClothingService service;

    @PostMapping
    public ResponseEntity<Clothing> add(@RequestBody ClothingDTO dto) {
        Clothing c = new Clothing(dto.getName(), dto.getSize(), dto.getColor(), dto.getPrice());
        return ResponseEntity.ok(service.saveClothing(c));
    }

    @GetMapping
    public List<Clothing> getAll() {
        return service.getAll();
    }
}
