package com.github.duskmage2009.entity;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "cards",
        indexes = {
                @Index(name = "idx_card_name", columnList = "name"),
                @Index(name = "idx_card_deck", columnList = "deck_id"),
                @Index(name = "idx_card_type", columnList = "type"),
                @Index(name = "idx_card_faction", columnList = "faction")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deck_id", nullable = false)
    private Deck deck;

    @Column(nullable = false)
    private Integer provision;

    @Column(nullable = false)
    private Integer power;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private CardType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Faction faction;

    @Column(length = 500)
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}