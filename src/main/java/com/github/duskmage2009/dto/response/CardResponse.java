package com.github.duskmage2009.dto.response;



import com.github.duskmage2009.entity.Card;
import com.github.duskmage2009.entity.CardType;
import com.github.duskmage2009.entity.Faction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardResponse {

    private Long id;
    private String name;
    private DeckResponse deck;
    private Integer provision;
    private Integer power;
    private CardType type;
    private Faction faction;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CardResponse from(Card card) {
        return CardResponse.builder()
                .id(card.getId())
                .name(card.getName())
                .deck(DeckResponse.from(card.getDeck()))
                .provision(card.getProvision())
                .power(card.getPower())
                .type(card.getType())
                .faction(card.getFaction())
                .description(card.getDescription())
                .createdAt(card.getCreatedAt())
                .updatedAt(card.getUpdatedAt())
                .build();
    }
}