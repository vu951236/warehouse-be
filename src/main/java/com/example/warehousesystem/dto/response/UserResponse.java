package com.example.warehousesystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Integer userId;
    private String username;
    private String email;
    private String fullName;
    private String role;
    private Boolean isActive;
}
