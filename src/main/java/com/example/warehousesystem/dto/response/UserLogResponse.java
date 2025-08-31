package com.example.warehousesystem.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserLogResponse {
    private String userName;
    private String action;
    private String targetTable;
    private String timestamp;
}
