package com.example.warehousesystem.blockchain;

import org.springframework.stereotype.Service;
import com.example.warehousesystem.entity.Clothing;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

@Service
public class BlockchainService {
    private List<String> blockchain = new ArrayList<>();

    public void addToBlockchain(Clothing clothing) {
        String data = clothing.toString() + LocalDateTime.now();
        blockchain.add(data);
        System.out.println("Đã thêm vào blockchain: " + data);
    }

    public List<String> getBlockchain() {
        return blockchain;
    }
}
