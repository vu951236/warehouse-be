package com.example.warehousesystem.mapper;

import com.example.warehousesystem.dto.response.AllocationResponse;
import com.example.warehousesystem.entity.Box;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AllocationMapper {

    public AllocationResponse toResponse(String skuCode, int totalAllocated, List<AllocationResponse.AllocationDetail> list) {
        return AllocationResponse.builder()
                .skuCode(skuCode)
                .totalAllocated(totalAllocated)
                .allocations(list)
                .build();
    }

    public AllocationResponse.AllocationDetail toDetail(Box box, int allocatedQty) {
        return AllocationResponse.AllocationDetail.builder()
                .boxId(box.getId())
                .binCode(box.getBin().getBinCode())
                .allocatedQuantity(allocatedQty)
                .remainingCapacity(box.getCapacity() - box.getUsedCapacity() - allocatedQty)
                .build();
    }
}



