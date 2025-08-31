package com.example.warehousesystem.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "export_log")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExportLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "export_code")
    private String exportCode;

    @Column(name = "sku_code")
    private String skuCode;

    private int quantity;

    @Column(name = "exported_by")
    private Long exportedBy;

    @Column(name = "export_date")
    private LocalDateTime exportDate;

    @Column(name = "export_date_string")
    private String exportDateString;

    private String destination;
    private String status;
    private String source;
    private Boolean urgent;

    private String note;

    @Column(name = "picking_route", columnDefinition = "jsonb")
    private String pickingRoute;
}
