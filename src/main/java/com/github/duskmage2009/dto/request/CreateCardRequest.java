package com.github.duskmage2009.dto.request;

import com.github.duskmage2009.entity.CardType;
import com.github.duskmage2009.entity.Faction;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateCardRequest {

    @NotBlank(message = "Card name is required")
    @Size(min = 2, max = 200, message = "Card name must be between 2 and 200 characters")
    private String name;

    @NotNull(message = "Deck ID is required")
    @Positive(message = "Deck ID must be positive")
    private Long deckId;

    @NotNull(message = "Provision is required")
    @Min(value = 0, message = "Provision cannot be negative")
    @Max(value = 20, message = "Provision cannot exceed 20")
    private Integer provision;

    @NotNull(message = "Power is required")
    @Min(value = 0, message = "Power cannot be negative")
    @Max(value = 50, message = "Power cannot exceed 50")
    private Integer power;

    @NotNull(message = "Card type is required")
    private CardType type;

    @NotNull(message = "Faction is required")
    private Faction faction;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
}