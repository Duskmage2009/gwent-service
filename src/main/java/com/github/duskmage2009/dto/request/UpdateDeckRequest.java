package com.github.duskmage2009.dto.request;


import com.github.duskmage2009.entity.Faction;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UpdateDeckRequest {

    @NotBlank(message = "Deck name is required")
    @Size(min = 3, max = 200, message = "Deck name must be between 3 and 200 characters")
    private String name;

    @NotNull(message = "Faction is required")
    private Faction faction;

    @NotBlank(message = "Leader ability is required")
    @Size(min = 3, max = 200, message = "Leader ability must be between 3 and 200 characters")
    private String leaderAbility;

    @NotNull(message = "Provision limit is required")
    @Min(value = 100, message = "Provision limit must be at least 100")
    @Max(value = 200, message = "Provision limit must be at most 200")
    private Integer provisionLimit;

    @Size(max = 500, message = "Categories must not exceed 500 characters")
    private String categories;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
}