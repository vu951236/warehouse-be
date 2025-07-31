package com.example.warehousesystem.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "box")
public class Box {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bin_id", nullable = false)
    private Bin bin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sku_id", nullable = false)
    private SKU sku;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Column(name = "used_capacity", nullable = false)
    private Integer usedCapacity = 0;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;
}
