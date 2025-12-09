package com.github.duskmage2009.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.duskmage2009.BaseIntegrationTest;
import com.github.duskmage2009.dto.request.CreateDeckRequest;
import com.github.duskmage2009.dto.request.UpdateDeckRequest;
import com.github.duskmage2009.entity.Faction;
import com.github.duskmage2009.repository.DeckRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
class DeckControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DeckRepository deckRepository;

    @Test
    void shouldGetAllDecks() throws Exception {
        mockMvc.perform(get("/api/decks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[0].faction").exists());
    }

    @Test
    void shouldGetDeckById() throws Exception {
        mockMvc.perform(get("/api/decks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.faction").exists())
                .andExpect(jsonPath("$.leaderAbility").exists())
                .andExpect(jsonPath("$.provisionLimit").exists());
    }

    @Test
    void shouldReturnNotFoundForNonExistentDeck() throws Exception {
        mockMvc.perform(get("/api/decks/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Deck not found with id: 999"));
    }

    @Test
    void shouldCreateDeck() throws Exception {
        CreateDeckRequest request = new CreateDeckRequest();
        request.setName("Test Deck");
        request.setFaction(Faction.MONSTERS);
        request.setLeaderAbility("Test Ability");
        request.setProvisionLimit(165);
        request.setCategories("Test");
        request.setDescription("Test description");

        mockMvc.perform(post("/api/decks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Test Deck"))
                .andExpect(jsonPath("$.faction").value("Monsters"))
                .andExpect(jsonPath("$.leaderAbility").value("Test Ability"))
                .andExpect(jsonPath("$.provisionLimit").value(165));
    }

    @Test
    void shouldReturnBadRequestForInvalidDeck() throws Exception {
        CreateDeckRequest request = new CreateDeckRequest();
        request.setName("AB"); // Too short
        request.setFaction(Faction.MONSTERS);
        request.setLeaderAbility("Test");
        request.setProvisionLimit(50); // Too low

        mockMvc.perform(post("/api/decks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validationErrors").exists());
    }

    @Test
    void shouldReturnConflictForDuplicateDeckName() throws Exception {
        CreateDeckRequest request = new CreateDeckRequest();
        request.setName("Monster Swarm"); // Already exists in liquibase
        request.setFaction(Faction.MONSTERS);
        request.setLeaderAbility("Test Ability");
        request.setProvisionLimit(165);

        mockMvc.perform(post("/api/decks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"));
    }

    @Test
    void shouldUpdateDeck() throws Exception {
        UpdateDeckRequest request = new UpdateDeckRequest();
        request.setName("Updated Monster Swarm");
        request.setFaction(Faction.MONSTERS);
        request.setLeaderAbility("Updated Ability");
        request.setProvisionLimit(170);
        request.setCategories("Updated");
        request.setDescription("Updated description");

        mockMvc.perform(put("/api/decks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Monster Swarm"))
                .andExpect(jsonPath("$.provisionLimit").value(170));
    }

    @Test
    void shouldDeleteDeck() throws Exception {
        // Создаем тестовую колоду
        CreateDeckRequest createRequest = new CreateDeckRequest();
        createRequest.setName("Deck To Delete");
        createRequest.setFaction(Faction.NEUTRAL);
        createRequest.setLeaderAbility("Test");
        createRequest.setProvisionLimit(150);

        String response = mockMvc.perform(post("/api/decks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long deckId = objectMapper.readTree(response).get("id").asLong();

        // Удаляем
        mockMvc.perform(delete("/api/decks/" + deckId))
                .andExpect(status().isNoContent());

        // Проверяем что удалилась
        mockMvc.perform(get("/api/decks/" + deckId))
                .andExpect(status().isNotFound());
    }
}