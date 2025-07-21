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
@Table(name = "importorder")
public class ImportOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

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
        factory,
        returnGoods;

        @Override
        public String toString() {
            return this == returnGoods ? "return" : name();
        }
    }

    public enum Status {
        draft,
        confirmed,
        cancelled
    }
}
