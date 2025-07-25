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
@Table(name = "exportorder")
public class ExportOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "order_code", nullable = false, length = 100)
    private String orderCode;

    @Column(name = "destination", nullable = false, length = 255)
    private String destination;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false, length = 20)
    private Source source;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    public enum Source {
        manual,
        haravan
    }

    public enum Status {
        draft,
        confirmed,
        cancelled
    }
}
