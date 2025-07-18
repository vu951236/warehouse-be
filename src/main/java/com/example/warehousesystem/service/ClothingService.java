package com.example.warehousesystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import com.example.warehousesystem.entity.Clothing;
import com.example.warehousesystem.repository.ClothingRepository;
import com.example.warehousesystem.blockchain.BlockchainService;

@Service
public class ClothingService {
    @Autowired
    private ClothingRepository repository;

    @Autowired
    private BlockchainService blockchain;

    public Clothing saveClothing(Clothing clothing) {
        Clothing saved = repository.save(clothing);
        blockchain.addToBlockchain(saved);
        return saved;
    }

    public List<Clothing> getAll() {
        return repository.findAll();
    }
}
