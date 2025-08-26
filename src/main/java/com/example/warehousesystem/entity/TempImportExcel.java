package com.example.warehousesystem.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "temp_import_excel")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TempImportExcel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "sku_code", nullable = false)
    private String skuCode;

    private Integer quantity;

    private String source;

    private String note;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDate.now();
    }

    @Column(name = "import_code", nullable = false)
    private String importCode;

}
