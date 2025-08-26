package com.example.warehousesystem.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "damaged_item")
public class DamagedItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "sku_id", nullable = false)
    private SKU sku;

    @Column(name = "barcode", nullable = false, length = 100)
    private String barcode;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "marked_at", nullable = false)
    private LocalDate markedAt;

    @Column(name = "note", length = 255)
    private String note;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;
}
