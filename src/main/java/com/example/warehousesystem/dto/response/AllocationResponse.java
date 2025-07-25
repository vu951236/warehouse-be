package com.example.warehousesystem.dto.response;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AllocationResponse {
    private String skuCode;
    private Integer totalAllocated;
    private List<AllocationDetail> allocations;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AllocationDetail {
        private Integer boxId;
        private String binCode;
        private Integer allocatedQuantity;
        private Integer remainingCapacity;
    }
}



