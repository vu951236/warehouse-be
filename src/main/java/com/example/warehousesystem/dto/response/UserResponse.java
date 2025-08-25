package com.example.warehousesystem.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Integer userId;
    private String userCode;
    private String username;
    private String email;
    private String fullName;
    private String role;
    private Boolean isActive;

    @JsonFormat(pattern = "yyyy/MM/dd")
    private LocalDateTime createdAt;
}
