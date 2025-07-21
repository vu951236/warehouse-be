package com.example.warehousesystem.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "sku")
public class SKU {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "sku_code", nullable = false, unique = true, length = 50)
    private String skuCode;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "size", length = 10)
    private String size;

    @Column(name = "color", length = 10)
    private String color;

    @Column(name = "type", length = 50)
    private String type;

    @Column(name = "unit_volume")
    private Float unitVolume = 1.0f;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
