package com.example.warehousesystem.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserLockResponse {
    private Integer userId;
    private String username;
    private Boolean isActive;
}
