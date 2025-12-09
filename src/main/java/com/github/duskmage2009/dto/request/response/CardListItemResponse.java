package com.github.duskmage2009.dto.request.response;


import com.github.duskmage2009.entity.Card;
import com.github.duskmage2009.entity.CardType;
import com.github.duskmage2009.entity.Faction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardListItemResponse {

    private Long id;
    private String name;
    private String deckName;
    private Integer provision;
    private Integer power;
    private CardType type;
    private Faction faction;

    public static CardListItemResponse from(Card card) {
        return CardListItemResponse.builder()
                .id(card.getId())
                .name(card.getName())
                .deckName(card.getDeck().getName())
                .provision(card.getProvision())
                .power(card.getPower())
                .type(card.getType())
                .faction(card.getFaction())
                .build();
    }
}