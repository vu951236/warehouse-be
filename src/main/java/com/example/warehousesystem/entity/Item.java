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
@Table(name = "item")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "box_id", nullable = false)
    private Box box;

    @ManyToOne
    @JoinColumn(name = "sku_id", nullable = false)
    private SKU sku;

    @Column(name = "barcode", nullable = false, unique = true, length = 100)
    private String barcode;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private Status status;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "created_at")
    private LocalDate createdAt;

    public enum Status {
        available,
        damaged,
        returned,
        exported,
        queued // trạng thái chờ xuất
    }

    @Column(name = "note", length = 255)
    private String note;

    @Column(name = "transferred_at")
    private LocalDateTime transferredAt;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;
}
