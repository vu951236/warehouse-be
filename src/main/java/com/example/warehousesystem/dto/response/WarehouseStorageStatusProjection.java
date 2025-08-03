package com.example.warehousesystem.dto.response;

public interface WarehouseStorageStatusProjection {
    String getWarehouseName();       // raw[0]
    Double getUsedCapacity();        // raw[1]
    Integer getShelfCount();         // raw[2]
    Double getTotalBinCount();       // raw[3]
    Double getBinCapacity();         // raw[4]
}
