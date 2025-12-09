package com.github.duskmage2009.dto.request;

import com.github.duskmage2009.entity.CardType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.Data;

@Data
public class CardListRequest {

    private Long deckId;

    private CardType type;

    @Min(value = 0, message = "Minimum power cannot be negative")
    private Integer minPower;

    @Min(value = 1, message = "Page must be at least 1")
    private Integer page = 1;

    @Min(value = 1, message = "Size must be at least 1")
    @Max(value = 100, message = "Size cannot exceed 100")
    private Integer size = 20;
}