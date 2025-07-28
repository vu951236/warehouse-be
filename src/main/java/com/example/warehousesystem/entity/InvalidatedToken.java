package com.example.warehousesystem.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "invalidated_token")
public class InvalidatedToken {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "expiry_time")
    @Temporal(TemporalType.TIMESTAMP) // Giúp mapping chính xác kiểu thời gian
    private Date expiryTime;
}
