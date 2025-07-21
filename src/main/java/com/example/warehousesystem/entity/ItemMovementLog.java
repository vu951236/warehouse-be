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
@Table(name = "itemmovementlog")
public class ItemMovementLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 20)
    private Action action;

    @Column(name = "source_box_id")
    private Integer sourceBoxId;

    @Column(name = "dest_box_id")
    private Integer destBoxId;

    @Column(name = "related_order", length = 100)
    private String relatedOrder;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    public enum Action {
        import_,
        export,
        relocate,
        return_,
        damage;

        @Override
        public String toString() {
            return switch (this) {
                case import_ -> "import";
                case return_ -> "return";
                default -> name();
            };
        }
    }
}
