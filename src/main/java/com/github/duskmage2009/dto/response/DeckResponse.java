package com.github.duskmage2009.dto.response;



import com.github.duskmage2009.entity.Deck;
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
public class DeckResponse {

    private Long id;
    private String name;
    private Faction faction;
    private String leaderAbility;
    private Integer provisionLimit;
    private String categories;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static DeckResponse from(Deck deck) {
        return DeckResponse.builder()
                .id(deck.getId())
                .name(deck.getName())
                .faction(deck.getFaction())
                .leaderAbility(deck.getLeaderAbility())
                .provisionLimit(deck.getProvisionLimit())
                .categories(deck.getCategories())
                .description(deck.getDescription())
                .createdAt(deck.getCreatedAt())
                .updatedAt(deck.getUpdatedAt())
                .build();
    }
}